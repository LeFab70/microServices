# e-commerce-app2

Projet d'apprentissage : plateforme e-commerce en microservices avec Spring Boot 4 / Spring Cloud.

## Architecture

```
                         ┌───────────────┐   ┌───────────────┐
                         │  discovery    │   │  config-server│
                         │  (Eureka)     │   │               │
                         │  :8761        │   │  :8888        │
                         └───────────────┘   └───────────────┘
                                 ▲                    ▲
                     ┌───────────┼────────────────────┼───────────┐
                     │           │                    │           │
              ┌──────┴─────┐  ┌──┴─────────┐   ┌──────┴─────┐  (à venir)
              │ customer   │  │ product    │   │ order      │  payment
              │ :8090      │  │ :8091      │◄──┤ :8092      │  notification
              │ MongoDB    │  │ PostgreSQL │   │ PostgreSQL │  api-gateway
              └────────────┘  └────────────┘   └─────┬──────┘
                     ▲                                │
                     └────────────────────────────────┘
                          appels Feign (customer + product)
```

Chaque service est enregistré auprès de `discovery` (Eureka) et récupère sa configuration depuis `config-server` au démarrage (`spring.config.import: optional:configserver:...`). `order-service` est l'orchestrateur : il ne connaît que les APIs publiques de `customer-service`/`product-service` (via OpenFeign), jamais leurs bases de données.

### Domaines (voir `diagrams/`)

| Domaine | Service | Statut |
|---|---|---|
| Customer (+ Address) | `customer-service` | ✅ fait |
| Product (+ Category) | `product-service` | ✅ fait |
| Order (+ OrderLine) | `order-service` | ✅ fait — orchestrateur : Feign vers `customer`/`product`, réserve le stock via `POST /product/purchase` |
| Payment | `payment-service` | 🚧 à créer |
| Notification | `notification-service` | 🚧 à créer — probablement déclenché en asynchrone via Kafka après un achat confirmé |
| API Gateway | `api-gateway` | 🚧 à créer — point d'entrée unique pour le frontend Angular |

## Services

| Service | Port | Base de données | Rôle |
|---|---|---|---|
| `discovery` | 8761 | — | Annuaire de services (Eureka) |
| `config-server` | 8888 | — | Configuration centralisée |
| `customer` | 8090 | MongoDB (`customer-db`) | Gestion des clients (imbrique `Address`) |
| `product` | 8091 | PostgreSQL (`product-db`) | Catalogue produit, catégories, stock, achat |
| `order` | 8092 | PostgreSQL (`order-db`) | Commandes — orchestre `customer` + `product` via Feign |

Pas de `pom.xml` racine — chaque service est un module Maven indépendant, à builder/lancer séparément. Un README détaillé existe dans chaque `services/<nom>/README.md`.

## Infrastructure (Docker)

```bash
docker compose up -d
```

| Service | Port hôte | Usage |
|---|---|---|
| `postgresql` | 5433 → 5432 | Bases `product-db` et `order-db` (même instance) |
| `pgadmin` | 5050 | UI d'admin PostgreSQL |
| `mongodb` | 27018 → 27017 | Base `customer-db` (port décalé pour éviter le conflit avec un MongoDB local) |
| `mongo-express` | 8081 | UI d'admin MongoDB |
| `kafka` / `zookeeper` | 9092 / 22181 | Messagerie événementielle (prévu pour `notification-service`) |
| `mail-dev` | 1080 (UI), 1025 (SMTP) | Serveur mail de dev, pour tester les emails de confirmation |
| `zipkin` | 9411 | Traçage distribué |

## Démarrage (ordre à respecter)

```bash
docker compose up -d

cd services/discovery && mvn spring-boot:run &
cd services/config-server && mvn spring-boot:run &

# attendre que les deux soient up (http://localhost:8761, http://localhost:8888)

cd services/customer && mvn spring-boot:run &
cd services/product && mvn spring-boot:run &
cd services/order && mvn spring-boot:run &
```

Ou lancer chaque `*Application` depuis IntelliJ dans le même ordre — penser à activer **"Single instance only"** sur chaque Run Configuration pour éviter les conflits de port en cas de double lancement.

## Tester

Une collection Postman est disponible avec des exemples pour les trois services (`customer`, `product`/`category`, `order`) — voir `resources/` ou demander la dernière version.

Exemple de création de commande (`order-service`) :
```bash
curl -X POST http://localhost:8092/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "<id client existant>",
    "orderLines": [{ "productId": 1, "quantity": 2 }]
  }'
```

## Documentation API

- `product-service` publie une doc Swagger : http://localhost:8091/swagger-ui/index.html
- `customer-service`/`order-service` n'en ont pas encore.

## Stack technique

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Cloud Config + Netflix Eureka (service discovery)
- OpenFeign (communication inter-services, ex: `order` → `customer`/`product`)
- Spring Data MongoDB (`customer`) / Spring Data JPA + Flyway (`product`, `order`)
- MapStruct (mapping entité ↔ DTO), Lombok
- springdoc-openapi (Swagger, `product-service` uniquement pour l'instant)
- Docker Compose pour l'infra (PostgreSQL, MongoDB, Kafka, mail-dev, Zipkin)

## Documentation par service

- [services/discovery/README.md](services/discovery/README.md)
- [services/config-server/README.md](services/config-server/README.md)
- [services/customer/README.md](services/customer/README.md)
- [services/product/README.md](services/product/README.md)
- [services/order/README.md](services/order/README.md)
