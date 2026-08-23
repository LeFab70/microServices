# order-service

Microservice orchestrateur : crée une commande (`Order` + `OrderLine`) en validant le client et en réservant le stock auprès des autres services, via OpenFeign + Eureka (aucune URL codée en dur).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Data JPA + PostgreSQL + Flyway
- OpenFeign (appels vers `customer-service` et `product-service`)
- MapStruct pour le mapping entité ↔ DTO
- Spring Cloud Config (config-server) + Eureka (discovery)

## Prérequis

Ce service dépend des **trois autres** services pour fonctionner réellement (pas juste démarrer) :

| Service | Port | Rôle pour `order-service` |
|---|---|---|
| `discovery` | 8761 | résolution des noms `customer-service`/`product-service` via Feign |
| `config-server` | 8888 | fournit `order-service.yml` (datasource, port) |
| `customer-service` | 8090 | validation du client (`GET /api/v1/customer/{id}`) |
| `product-service` | 8091 | réservation de stock (`POST /api/v1/product/purchase`) |
| PostgreSQL (Docker) | 5433 | base `order-db` |

## Lancer le service

```bash
cd services/order
mvn spring-boot:run
```

Port **8092**. Flyway applique les migrations automatiquement au démarrage.

## Endpoints (`/api/v1/orders`)

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/v1/orders` | Liste toutes les commandes |
| POST | `/api/v1/orders` | Créer une commande |

### Créer une commande

```json
{
  "customerId": "6a8a4f7772607375303484d6",
  "orderLines": [
    { "productId": 1, "quantity": 2 },
    { "productId": 51, "quantity": 1 }
  ]
}
```

Déroulement de `createOrder` :
1. `CustomerRestClient.findCustomerById(customerId)` (Feign → `customer-service`) — valide que le client existe, récupère son nom.
2. `ProductRestClient.purchase(orderLines)` (Feign → `product-service`) — réserve/décrémente le stock pour toutes les lignes en un seul appel ; renvoie nom + prix au moment de l'achat.
3. `Order` + `OrderLine` sont construits et sauvegardés (`cascade = ALL` sur la relation, un seul `save()` suffit).
4. La réponse combine les données déjà en main (pas de second appel Feign nécessaire à la création).

Réponse :
```json
{
  "id": 1,
  "orderDate": "2026-08-22T23:04:47.267974",
  "lastUpdate": "2026-08-22T23:04:47.267974",
  "reference": "7d0d1345-5369-40ee-957c-2098ca2d2e7f",
  "status": "PENDING",
  "customer": { "id": "6a8a4f7772607375303484d6", "firstName": "Jean", "lastName": "Tremblay" },
  "orderLine": [
    { "id": 1, "productId": 1, "productName": "Product 1", "quantity": 2.0, "price": 156.35 }
  ]
}
```

## Gestion des erreurs venant des services distants

`OrderEntity`/`OrderLine` ne dupliquent aucune règle métier de `customer-service`/`product-service`. Un `GlobalExceptionHandler` dédié (`@ExceptionHandler(FeignException.class)`) intercepte toute erreur renvoyée par un appel Feign (client introuvable, stock insuffisant, produit introuvable, service injoignable) et relaie le **vrai statut HTTP et message** du service en amont, plutôt que de planter avec un 500 générique.

## Design des clés (important)

- `Order` ↔ `OrderLine` : vraie relation JPA (`@OneToMany`/`@ManyToOne`), même base de données.
- `Order.customerId` : `String` brut (id Mongo de `customer-service`), **pas** de `@ManyToOne` — `Customer` vit dans un autre service/une autre base.
- `OrderLine.productId` : `Long` brut (id Postgres de `product-service`), même raison.

Ces deux derniers ids sont validés/enrichis via Feign, jamais via une relation JPA inter-service.
