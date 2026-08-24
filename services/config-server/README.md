# config-server

Serveur de configuration centralisée (Spring Cloud Config). Sert la configuration de chaque microservice depuis des fichiers YAML locaux (`classpath:/configurations/`) — pas de dépôt Git distant pour ce projet.

> ⚠️ Pas de JWT/Keycloak, pas de rate limiting. Expose `/actuator/prometheus` (scrapé par Prometheus/Grafana). Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Cloud Config Server (backend natif, fichiers en `src/main/resources/configurations/`)

## Lancer le service

Démarre juste après `discovery`.

```bash
cd services/config-server
mvn spring-boot:run
```

Port **8888**.

## Fichiers de configuration

| Fichier | Sert la config de | Port du service |
|---|---|---|
| `configurations/application.yml` | **Tous les services** (config partagée : `eureka.client.serviceUrl`) | — |
| `configurations/discovery-service.yaml` | `discovery` | 8761 |
| `configurations/customer-service.yml` | `customer` | 8090 |
| `configurations/product-service.yml` | `product` | 8091 |
| `configurations/order-service.yml` | `order` | 8092 |

Chaque service demande sa config au démarrage via `spring.config.import: optional:configserver:http://localhost:8888` dans son propre `application.yml`, en utilisant `spring.application.name` comme clé de correspondance avec le nom du fichier (`customer-service` → `customer-service.yml`).

## Vérifier ce qu'un service va recevoir

```bash
curl http://localhost:8888/order-service/default
```

Renvoie le JSON fusionné (`order-service.yml` + `application.yml`) — utile pour déboguer un service qui ne démarre pas avec la bonne config, sans avoir à le lancer lui-même.

## Piège connu (déjà rencontré sur ce projet)

Si un service semble ignorer sa config (ex: se connecte toujours sur des valeurs par défaut au lieu de celles de `config-server`), vérifier :
1. Que `config-server` répond bien via la commande `curl` ci-dessus.
2. Que `spring.application.name` du service correspond **exactement** au nom du fichier YAML (sans le suffixe).
3. Le préfixe des propriétés attendu par la version de Spring Boot utilisée (ex: `spring.mongodb.*` et non `spring.data.mongodb.*` en Spring Boot 4.1 — ça a déjà piégé ce projet une fois).
