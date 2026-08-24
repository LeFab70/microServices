# gateway-service

Point d'entrée HTTP unique de la plateforme — route les requêtes entrantes vers `customer`/`product`/`order`/`payment` en résolvant leur adresse via Eureka (`lb://`).

> ⚠️ Endpoints ouverts publiquement — pas de JWT/Keycloak. Le filtre Bucket4j (rate limiting) est disponible dans la dépendance mais **pas configuré**. Expose `/actuator/prometheus` (scrapé par Prometheus/Grafana). Access log Tomcat activé (`logs/access_log.*.log`) — c'est ici que l'IP **réelle** du client apparaît pour tout appel entrant via le Gateway ; les services en aval ne voient que l'IP du Gateway. Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- **Spring Cloud Gateway MVC** (`spring-cloud-starter-gateway-server-webmvc`) — variante Spring MVC classique, pas WebFlux
- Spring Cloud Config (config-server) + Eureka (discovery)

## ⚠️ Piège important : pas de "discovery locator" ici

Le Gateway **réactif** (`spring-cloud-starter-gateway`, WebFlux) a une fonctionnalité qui crée automatiquement une route `/{nom-du-service}/**` pour chaque service enregistré dans Eureka (`spring.cloud.gateway.discovery.locator.enabled: true`). **Cette fonctionnalité n'existe pas** dans la variante MVC utilisée ici (aucune classe `Discovery`/`Locator` dans le jar `spring-cloud-gateway-server-webmvc`) — il faut déclarer les routes **explicitement**.

Contrairement à ce qu'on pourrait attendre, il n'y a donc pas de préfixe `/customer-service/...` sur les routes — le Gateway expose directement le même chemin que le service cible :

```
http://localhost:8222/api/v1/customer  →  customer-service:/api/v1/customer
http://localhost:8222/api/v1/orders    →  order-service:/api/v1/orders
```

## Prérequis

| Service | Port | Rôle |
|---|---|---|
| `discovery` | 8761 | résolution des `lb://` vers les instances réelles |
| `config-server` | 8888 | fournit `gateway-service.yml` (les routes) |
| `customer`/`product`/`order`/`payment` | 8090-8093 | doivent être démarrés et enregistrés dans Eureka **avant** de tester une route, sinon 503 |

## Lancer le service

```bash
cd services/gateway
mvn spring-boot:run
```

Port **8222**.

## Routes déclarées

Dans `config-server` → `gateway-service.yml`, sous `spring.cloud.gateway.server.webmvc.routes` :

| Route id | Predicate | Cible |
|---|---|---|
| `customer-service` | `Path=/api/v1/customer/**` | `lb://customer-service` |
| `product-service` | `Path=/api/v1/product/**,/api/v1/category/**` | `lb://product-service` |
| `order-service` | `Path=/api/v1/orders/**` | `lb://order-service` |
| `payment-service` | `Path=/api/v1/payment/**` | `lb://payment-service` |

Pour ajouter une route (ex: `notification-service`, s'il expose un jour une API REST), ajouter une entrée dans cette même liste — pas de redémarrage du Gateway nécessaire pour *voir* le changement dans `config-server`, mais il faut redémarrer `gateway-service` pour recharger sa config (pas de refresh à chaud configuré).

## Tester

```bash
curl -X POST http://localhost:8222/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{ "customerId": "<id client existant>", "orderLines": [{ "productId": 1, "quantity": 2 }] }'
```
