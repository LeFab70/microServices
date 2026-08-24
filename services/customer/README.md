# customer-service

Microservice responsable des clients (`Customer`) et de leur adresse (`Address`, imbriquée dans le document Mongo — pas une collection séparée).

> ⚠️ Endpoints ouverts publiquement — pas de JWT/Keycloak, pas de rate limiting. Expose `/actuator/health` et `/actuator/prometheus` (scrapé par Prometheus/Grafana). Access log Tomcat activé (`logs/access_log.*.log` — IP client, méthode, URI, statut ; voir [README racine](../../README.md#observabilité-prometheus--grafana) pour le piège reverse-proxy via `gateway-service`). Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Data MongoDB
- MapStruct pour le mapping entité ↔ DTO
- Spring Cloud Config (config-server) + Eureka (discovery)
- springdoc-openapi (Swagger UI)

## Prérequis

| Service | Port |
|---|---|
| `discovery` | 8761 |
| `config-server` | 8888 |
| MongoDB (Docker) | 27018 (mappé depuis 27017 du conteneur) |

```bash
docker compose up -d mongodb
```

⚠️ Le port Mongo est décalé sur **27018** côté hôte (au lieu du 27017 par défaut) car une instance MongoDB native tourne déjà en local sur cette machine de dev — voir `services/config-server/configurations/customer-service.yml`.

## Lancer le service

```bash
cd services/customer
mvn spring-boot:run
```

Port **8090**.

## Documentation API (Swagger)

- Swagger UI : http://localhost:8090/swagger-ui/index.html
- OpenAPI JSON : http://localhost:8090/v3/api-docs

## Endpoints (`/api/v1/customer`)

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/v1/customer` | Liste tous les clients |
| GET | `/api/v1/customer/{id}` | Récupérer un client par id (id Mongo, `String`) |
| POST | `/api/v1/customer` | Créer un client (avec `address` imbriquée) |
| PUT | `/api/v1/customer/{id}` | Mettre à jour un client |
| DELETE | `/api/v1/customer/{id}` | Suppression logique — passe `status` à `INACTIVE`, ne supprime pas le document |

## Format JSON

```json
{
  "firstName": "Adelaida",
  "lastName": "Sipes",
  "email": "adelaida@example.com",
  "address": {
    "street": "12 rue de la Paix",
    "city": "Paris",
    "state": "IDF",
    "zipCode": "75002"
  }
}
```

`address` est un objet **imbriqué** dans le document `customer` — ce n'est pas une relation, pas de collection Mongo séparée. C'est aussi ce service que `order-service` appelle en Feign (`GET /api/v1/customer/{id}`) pour valider un client avant de créer une commande.
