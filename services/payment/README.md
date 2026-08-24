# payment-service

Microservice de paiement : consomme les commandes créées par `order-service` via Kafka, crée un paiement (avec un instantané figé du client et des produits), et publie une confirmation de paiement.

> ⚠️ Endpoints ouverts publiquement — pas de JWT/Keycloak, pas de rate limiting. Expose `/actuator/health` et `/actuator/prometheus` (scrapé par Prometheus/Grafana). Access log Tomcat activé (`logs/access_log.*.log` — IP client, méthode, URI, statut ; voir [README racine](../../README.md#observabilité-prometheus--grafana) pour le piège reverse-proxy via `gateway-service`). Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Data JPA + PostgreSQL + Flyway
- Spring Kafka (`spring-kafka` + `spring-boot-starter-kafka`)
- MapStruct pour le mapping entité ↔ DTO
- Spring Cloud Config (config-server) + Eureka (discovery)
- Pas de Swagger pour l'instant (contrairement à `customer`/`product`/`order`)

## Prérequis

| Service | Port | Rôle |
|---|---|---|
| `discovery` | 8761 | annuaire de services |
| `config-server` | 8888 | fournit `payment-service.yml` |
| PostgreSQL (Docker) | 5433 | base `payment-db` |
| Kafka (Docker) | 29092 (listener hôte) | consomme `order-topic`, publie `payment-topic` |

⚠️ Même piège que `order-service` : `spring.kafka.bootstrap-servers` doit pointer sur `localhost:29092` (listener hôte), pas `kafka:9092` (interne aux conteneurs).

## Lancer le service

```bash
cd services/payment
mvn spring-boot:run
```

Port **8093**. Flyway applique les migrations automatiquement au démarrage.

## Flux Kafka

```
order-service --[order-topic]--> payment-service --[payment-topic]--> (notification-service, à venir)
```

`PaymentConsumer` écoute `order-topic` (groupe `payment-group`) et appelle `PaymentServices.createPayment(...)` pour chaque `OrderConfirmation` reçue :

1. Crée un `Payment` (statut `PENDING`) avec un **instantané figé** du client (`customerId`/`customerFirstName`/`customerLastName`) et des produits achetés (`PaymentItemEntity`, une ligne par produit) — pas d'appel réseau vers `order-service`/`customer-service` à la lecture, tout est déjà en base.
2. Publie une `PaymentConfirmation` sur `payment-topic`.

⚠️ **Désérialisation Kafka inter-services** : le producteur (`order-service`) et le consommateur (`payment-service`) ont chacun leur propre classe `OrderConfirmation` locale (packages différents). Par défaut, `JsonDeserializer` essaie de désérialiser en utilisant le header `__TypeId__` du message (qui contient le nom de classe **du producteur**) — ce qui échoue puisque cette classe n'existe pas côté consommateur. Il faut forcer l'utilisation du type local :
```yaml
spring:
  kafka:
    consumer:
      properties:
        spring.json.value.default.type: org.lefab.payment.dtos.OrderConfirmation
        spring.json.use.type.headers: false
```
Sans `spring.json.use.type.headers: false`, le message reste bloqué en boucle de retry indéfiniment (offset jamais commité) — symptôme observé : `payment-service` ne crée jamais de paiement, et `kafka-consumer-groups --describe --group payment-group` montre un lag qui n'avance pas.

## Endpoints (`/api/v1/payment`)

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/v1/payment?page=0&size=10` | Liste paginée des paiements |
| GET | `/api/v1/payment/{id}` | Récupérer un paiement |
| POST | `/api/v1/payment` | Création directe (hors flux Kafka normal — surtout pour tests manuels) |

## Migrations Flyway

- `V1__create_table_payment.sql` : table `payments` de base.
- `V2__add_customer_and_items.sql` : snapshot client (`customer_id`/`customer_first_name`/`customer_last_name`) + table `payment_item`.
