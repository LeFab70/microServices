# product-service

Microservice responsable du catalogue produit : catégories, produits, stock et achat (décrément atomique du stock, utilisé par `order-service`/`billing-service`).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Data JPA + PostgreSQL + Flyway
- MapStruct pour le mapping entité ↔ DTO
- Spring Cloud Config (config-server) + Eureka (discovery)
- springdoc-openapi (Swagger UI)

## Prérequis

Avant de lancer `product-service`, les services suivants doivent tourner :

| Service        | Port |
|----------------|------|
| `discovery`    | 8761 |
| `config-server`| 8888 |
| PostgreSQL (Docker, `docker-compose.yml`) | 5433 |

```bash
docker compose up -d postgresql
```

## Lancer le service

```bash
cd services/product
mvn spring-boot:run
```

Le service démarre sur le port **8091** (configuré via `config-server` → `product-service.yml`). Flyway applique automatiquement les migrations au démarrage.

## Documentation API (Swagger)

Une fois le service démarré :

- Swagger UI : http://localhost:8091/swagger-ui/index.html
- OpenAPI JSON : http://localhost:8091/v3/api-docs

## Endpoints

### Category (`/api/v1/category`)

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/v1/category?page=0&size=10` | Liste paginée des catégories actives |
| GET | `/api/v1/category/{id}` | Récupérer une catégorie |
| POST | `/api/v1/category` | Créer une catégorie |
| PUT | `/api/v1/category/{id}` | Mettre à jour une catégorie |
| DELETE | `/api/v1/category/{id}` | Suppression logique (soft delete, `active = false`) |

### Product (`/api/v1/product`)

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/v1/product?page=0&size=10` | Liste paginée des produits |
| GET | `/api/v1/product/{id}` | Récupérer un produit |
| POST | `/api/v1/product` | Créer un produit |
| POST | `/api/v1/product/purchase` | Réserver/décrémenter du stock pour une liste de `{productId, quantity}` (appelé par les services consommateurs, ex: `order-service`) |

## Gestion de la concurrence sur le stock

`POST /api/v1/product/purchase` décrémente `available_quantity` via une **requête UPDATE atomique et conditionnelle** au niveau base de données (`ProductRepository#decrementStock`) :

```sql
UPDATE product SET available_quantity = available_quantity - :quantity
WHERE id = :id AND available_quantity >= :quantity
```

La vérification de disponibilité et l'écriture se font dans la même requête SQL — aucune fenêtre de course n'est possible entre deux achats concurrents sur le même produit, sans avoir besoin de verrou explicite. Un champ `@Version` est aussi présent sur `ProductEntity` (verrouillage optimiste) pour protéger les autres chemins d'écriture.

Les requêtes de `/purchase` contenant des `productId` en double sont automatiquement agrégées (quantités sommées) avant traitement.

## Migrations Flyway

Situées dans `src/main/resources/db/migration/`. Une nouvelle modification de schéma doit toujours être ajoutée comme une **nouvelle** migration (`Vx__...sql`) — ne jamais modifier une migration déjà appliquée.
