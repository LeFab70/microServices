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
              │ :8090      │  │ :8091      │   │            │  notification
              │ MongoDB    │  │ PostgreSQL │   │            │
              └────────────┘  └────────────┘   └────────────┘
```

Chaque service est enregistré auprès de `discovery` (Eureka) et récupère sa configuration depuis `config-server` au démarrage (`spring.config.import: optional:configserver:...`).

### Domaines (voir `diagrams/`)

| Domaine | Service | Statut |
|---|---|---|
| Customer (+ Address) | `customer-service` | ✅ fait |
| Product (+ Category) | `product-service` | ✅ fait |
| Order (+ OrderLine) | `order-service` | 🚧 à créer — orchestrateur : appelle `customer`/`product` en Feign, réserve le stock via `POST /product/purchase` |
| Payment | `payment-service` | 🚧 à créer |
| Notification | `notification-service` | 🚧 à créer — probablement déclenché en asynchrone via Kafka après un achat confirmé |

## Services

| Service | Port | Base de données | Rôle |
|---|---|---|---|
| `discovery` | 8761 | — | Annuaire de services (Eureka) |
| `config-server` | 8888 | — | Configuration centralisée (fichiers dans `services/config-server/src/main/resources/configurations/`) |
| `customer` | 8090 | MongoDB (`customer-db`) | Gestion des clients (imbrique `Address`) |
| `product` | 8091 | PostgreSQL (`product-db`) | Catalogue produit, catégories, stock, achat |

Pas de `pom.xml` racine — chaque service est un module Maven indépendant, à builder/lancer séparément.

## Infrastructure (Docker)

```bash
docker compose up -d
```

| Service | Port hôte | Usage |
|---|---|---|
| `postgresql` | 5433 → 5432 | Base `product-db` |
| `pgadmin` | 5050 | UI d'admin PostgreSQL |
| `mongodb` | 27018 → 27017 | Base `customer-db` (port décalé pour éviter le conflit avec un MongoDB local) |
| `mongo-express` | 8081 | UI d'admin MongoDB |
| `kafka` / `zookeeper` | 9092 / 22181 | Messagerie événementielle (prévu pour `notification-service`) |
| `mail-dev` | 1080 (UI), 1025 (SMTP) | Serveur mail de dev, pour tester les emails de confirmation |
| `zipkin` | 9411 | Traçage distribué |

⚠️ Le port Mongo (27018) est volontairement décalé de son défaut (27017) car une instance MongoDB native tourne déjà en local sur cette machine.

## Démarrage (ordre à respecter)

```bash
docker compose up -d

cd services/discovery && mvn spring-boot:run &
cd services/config-server && mvn spring-boot:run &

# attendre que les deux soient up (http://localhost:8761, http://localhost:8888)

cd services/customer && mvn spring-boot:run &
cd services/product && mvn spring-boot:run &
```

Ou lancer chaque `*Application` depuis IntelliJ dans le même ordre.

## Documentation API

Chaque service qui expose une API REST publie sa propre doc Swagger (voir le README du service concerné), par exemple :
- `product-service` : http://localhost:8091/swagger-ui/index.html

## Stack technique

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Cloud Config + Netflix Eureka (service discovery)
- Spring Data MongoDB (`customer`) / Spring Data JPA + Flyway (`product`)
- MapStruct (mapping entité ↔ DTO), Lombok
- springdoc-openapi (Swagger)
- Docker Compose pour l'infra (PostgreSQL, MongoDB, Kafka, mail-dev, Zipkin)

## Documentation par service

- [services/product/README.md](services/product/README.md)
