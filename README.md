# e-commerce-app2

Projet d'apprentissage : plateforme e-commerce en microservices avec Spring Boot 4 / Spring Cloud.

## Architecture

```
                                          ┌───────────────┐
                             frontend --->│    gateway     │
                          (à venir)       │    :8222       │
                                          └───────┬───────┘
                                                  │ routes explicites (Spring MVC, pas de discovery locator)
                     ┌────────────┬───────────────┼───────────────┬────────────┐
                     │            │               │               │            │
                         ┌───────────────┐   ┌───────────────┐
                         │  discovery    │   │  config-server│
                         │  (Eureka)     │   │               │
                         │  :8761        │   │  :8888        │
                         └───────────────┘   └───────────────┘
                                 ▲                    ▲
                     ┌───────────┼────────────────────┼───────────┐
                     │           │                    │           │
              ┌──────┴─────┐  ┌──┴─────────┐   ┌──────┴─────┐
              │ customer   │  │ product    │   │ order      │
              │ :8090      │  │ :8091      │◄──┤ :8092      │
              │ MongoDB    │  │ PostgreSQL │   │ PostgreSQL │
              └──────▲─────┘  └────────────┘   └─────┬──────┘
                     │           appels Feign         │ publie order-topic
                     └────────────(customer+product)  ▼
                                                 ┌────────────┐
                                                 │   Kafka    │
                                                 └──┬──────┬──┘
                                     order-topic    │      │  order-topic + payment-topic
                                                     ▼      ▼
                                         ┌────────────┐  ┌──────────────┐
                                         │ payment    │  │ notification │
                                         │ :8093      │  │ :8094        │
                                         │ PostgreSQL │  │ MongoDB      │
                                         └─────┬──────┘  └──────┬───────┘
                                               │ publie          │ SMTP
                                               ▼ payment-topic   ▼
                                            (Kafka, ci-dessus)  mail-dev
```
(schéma simplifié — `gateway` route vers `customer`/`product`/`order`/`payment` via des routes déclarées explicitement, résolues par Eureka ; pas de frontend branché pour l'instant)

Chaque service est enregistré auprès de `discovery` (Eureka) et récupère sa configuration depuis `config-server` au démarrage (`spring.config.import: optional:configserver:...`). `order-service` est l'orchestrateur : il ne connaît que les APIs publiques de `customer-service`/`product-service` (via OpenFeign), jamais leurs bases de données.

### Domaines (voir `diagrams/`)

| Domaine | Service | Statut |
|---|---|---|
| Customer (+ Address) | `customer-service` | ✅ fait |
| Product (+ Category) | `product-service` | ✅ fait |
| Order (+ OrderLine) | `order-service` | ✅ fait — orchestrateur : Feign vers `customer`/`product`, réserve le stock via `POST /product/purchase`, publie un événement Kafka (`order-topic`) après création |
| Payment | `payment-service` | ✅ fait — consomme `order-topic` (Kafka), persiste un instantané client + produits, publie `payment-topic` |
| Notification | `notification-service` | ✅ fait — consomme `order-topic` + `payment-topic` (Kafka), trace en MongoDB, envoie un email (via `mail-dev`) |
| API Gateway | `gateway-service` | ✅ fait — routes explicites vers `customer`/`product`/`order`/`payment` (Spring Cloud Gateway MVC, résolution `lb://` via Eureka) ; pas encore branché à un frontend |

## Services

| Service | Port | Base de données | Rôle |
|---|---|---|---|
| `discovery` | 8761 | — | Annuaire de services (Eureka) |
| `config-server` | 8888 | — | Configuration centralisée |
| `customer` | 8090 | MongoDB (`customer-db`) | Gestion des clients (imbrique `Address`) |
| `product` | 8091 | PostgreSQL (`product-db`) | Catalogue produit, catégories, stock, achat |
| `order` | 8092 | PostgreSQL (`order-db`) | Commandes — orchestre `customer` + `product` via Feign |
| `payment` | 8093 | PostgreSQL (`payment-db`) | Paiements — consomme `order-topic` (Kafka), publie `payment-topic` |
| `notification` | 8094 | MongoDB (`notification-db`) | Notifications — consomme `order-topic`+`payment-topic`, envoie des emails via `mail-dev` |
| `gateway` | 8222 | — | Point d'entrée HTTP unique — routes explicites vers `customer`/`product`/`order`/`payment` |

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
| `kafka` / `zookeeper` | 9092 (interne conteneurs) / 29092 (hôte) / 22181 | Messagerie événementielle — `order-topic`, `payment-topic`, consommés par `payment`/`notification` |
| `kafka-ui` | 8085 | UI d'inspection des topics/messages/consumer groups Kafka |
| `mail-dev` | 1080 (UI), 1025 (SMTP) | Serveur mail de dev, pour tester les emails de confirmation |
| `zipkin` | 9411 | Traçage distribué (conteneur présent, mais aucun service ne lui envoie de traces pour l'instant — voir Limitations) |
| `prometheus` | 9090 | Scrape `/actuator/prometheus` de chaque service via `host.docker.internal` (config : `prometheus/prometheus.yml`) |
| `grafana` | 3000 | Dashboards de métriques — login `admin`/`admin`, ajouter Prometheus comme datasource (`http://prometheus:9090`) |

## Démarrage (ordre à respecter)

```bash
docker compose up -d

cd services/discovery && mvn spring-boot:run &
cd services/config-server && mvn spring-boot:run &

# attendre que les deux soient up (http://localhost:8761, http://localhost:8888)

cd services/customer && mvn spring-boot:run &
cd services/product && mvn spring-boot:run &
cd services/order && mvn spring-boot:run &
cd services/payment && mvn spring-boot:run &
cd services/notification && mvn spring-boot:run &

# attendre que customer/product/order/payment soient enregistrés dans Eureka avant de lancer gateway
cd services/gateway && mvn spring-boot:run &
```

Ou lancer chaque `*Application` depuis IntelliJ dans le même ordre — penser à activer **"Single instance only"** sur chaque Run Configuration pour éviter les conflits de port en cas de double lancement.

## Tester

Une collection Postman complète est disponible : [`resources/e-commerce-app2.postman_collection.json`](resources/e-commerce-app2.postman_collection.json). Elle couvre le CRUD complet (POST/GET/PUT/DELETE) de `category`, `product`, `customer`, `order`, `payment`, plus une section d'exemples via le Gateway.

Import dans Postman : **Import → File** → sélectionner le fichier. Les requêtes de création (POST) capturent automatiquement l'id créé dans une variable de collection (`customer_id`, `category_id`, `product_id`, `order_id`, `payment_id`) pour l'injecter dans les requêtes suivantes — exécuter les dossiers dans l'ordre (1. Category → 2. Product → 3. Customer → 4. Order → 5. Payment → 6. Via Gateway) via le **Collection Runner**, ou manuellement l'un après l'autre.

Chaque service a sa propre variable d'URL (`customer_url`, `product_url`, etc., par défaut sur ses ports directs) ; `gateway_url` (`8222`) est utilisé par la section "Via Gateway".

Exemple de création de commande (`order-service`, en direct) :
```bash
curl -X POST http://localhost:8092/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "<id client existant>",
    "orderLines": [{ "productId": 1, "quantity": 2 }]
  }'
```

Même appel via le Gateway (port `8222`, chemin identique — le Gateway ne réécrit pas le path) :
```bash
curl -X POST http://localhost:8222/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{ "customerId": "<id client existant>", "orderLines": [{ "productId": 1, "quantity": 2 }] }'
```

## Documentation API

Trois services métier publient une doc Swagger (pas encore fait sur `payment-service`) :

| Service | Swagger UI |
|---|---|
| `customer-service` | http://localhost:8090/swagger-ui/index.html |
| `product-service` | http://localhost:8091/swagger-ui/index.html |
| `order-service` | http://localhost:8092/swagger-ui/index.html |

## Observabilité (Prometheus + Grafana)

Chaque service expose `/actuator/prometheus` (nécessite d'avoir été redémarré après ajout de `micrometer-registry-prometheus` + config `management.endpoints.web.exposure.include`). Prometheus (conteneur Docker) scrape ces endpoints via `host.docker.internal` — les services tournent nativement sur la machine, pas dans Docker, même piège réseau que Mongo/Kafka rencontré plus tôt sur ce projet.

- Prometheus : http://localhost:9090 (page **Status → Targets** pour vérifier que chaque service est `UP`)
- Grafana : http://localhost:3000 (`admin`/`admin`) — ajouter Prometheus comme datasource : `http://prometheus:9090`

Chaque métrique est taguée `application=<nom-du-service>` (configuré via `management.metrics.tags.application` dans chaque `*-service.yml` sur `config-server`) pour pouvoir filtrer/grouper par service dans Grafana.

Le dashboard **JVM (Micrometer)** (importé dans Grafana, ID `4701`) a une section additionnelle **HTTP Endpoints** avec le débit et le total de requêtes par `method`/`uri`/`status`, filtrable par service via le sélecteur `Application` en haut du dashboard.

### Adresse IP du client : logs d'accès Tomcat, pas Prometheus

Prometheus est volontairement inadapté pour ça : chaque valeur de label crée une série temporelle distincte, et une IP client a une cardinalité potentiellement illimitée (autant de séries que de clients différents) — c'est un anti-pattern qui ferait exploser le stockage. Pour voir *quelle IP* a fait une requête, il faut les **logs d'accès**, pas les métriques.

`customer`/`product`/`order`/`payment`/`gateway-service` ont le **access log Valve de Tomcat** activé (`server.tomcat.accesslog.*` dans leur config sur `config-server`) — chaque requête HTTP est écrite dans `services/<nom>/logs/access_log.<date>.log` :
```
192.168.1.42 - - [24/Aug/2026:04:20:11] "GET /api/v1/customer HTTP/1.1" 200 512 14 ms
```

⚠️ **Piège reverse-proxy** : une requête passée par `gateway-service` (port `8222`) montrera l'IP du **Gateway** dans les logs du service cible, pas celle du client d'origine — c'est le comportement normal d'un reverse-proxy sans propagation d'en-tête (`X-Forwarded-For`), non configurée ici. Pour voir la vraie IP du client, regarder le log d'accès du service réellement contacté en premier (le Gateway si l'appel passe par lui, sinon le service direct).

Ces logs ne sont pas branchés dans Grafana — un pipeline de logs centralisé (Loki + Promtail par exemple) serait nécessaire pour les requêter depuis l'UI, volontairement hors scope de ce projet.

## ⚠️ Limitations / hors scope de ce projet

Ce projet est un TP d'apprentissage — plusieurs sujets volontairement **non traités**, à garder en tête :

- **Pas de sécurité applicative** : aucun JWT, aucun Keycloak/OAuth2, aucune authentification ni autorisation sur les endpoints. Toutes les routes sont ouvertes publiquement.
- **Pas de rate limiting** : aucune protection contre l'abus/spam d'un endpoint. `gateway-service` embarque le filtre Bucket4j de Spring Cloud Gateway (disponible dans la dépendance), mais **aucune limite n'est configurée** pour l'instant.
- **Métriques oui, tracing distribué non** : tous les services exposent `/actuator/prometheus` (Micrometer), scrapé par Prometheus et visualisable dans Grafana. Le conteneur `zipkin` tourne dans `docker-compose.yml`, mais **aucun service n'a la dépendance de tracing** (`micrometer-tracing`/`zipkin-reporter`) — impossible pour l'instant de suivre une requête à travers plusieurs services (ex: `order` → `product` → Kafka → `payment`).
- **Pas de WebFlux** : tous les services REST (`customer`/`product`/`order`/`payment`) utilisent Spring MVC classique (I/O bloquant, un thread par requête). Pas dimensionné pour un grand nombre de clients simultanés — un vrai besoin de scalabilité à fort trafic demanderait de repasser en réactif (WebFlux + driver Mongo/R2DBC réactifs).
- **Pas de WebSocket / suivi de commande en temps réel** : le statut d'une commande (`OrderStatus`) ne peut être consulté qu'en interrogeant `GET /api/v1/orders/{id}` (polling côté client) — aucun push serveur→client. Un vrai suivi de commande en direct demanderait un canal WebSocket (ou SSE) sur `order-service`, probablement alimenté par les mêmes événements Kafka déjà publiés (`order-topic`/`payment-topic`).

Si ce projet devait évoluer vers quelque chose de plus proche de la prod, ce sont les prochains sujets à traiter — pas juste API Gateway/Payment/Notification.

## Stack technique

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Cloud Config + Netflix Eureka (service discovery)
- Spring Cloud Gateway MVC (`spring-cloud-starter-gateway-server-webmvc`) — routes déclarées explicitement, pas de discovery locator (fonctionnalité propre à la variante WebFlux, absente ici)
- OpenFeign (communication inter-services, ex: `order` → `customer`/`product`)
- Spring Data MongoDB (`customer`, `notification`) / Spring Data JPA + Flyway (`product`, `order`, `payment`)
- Spring Kafka (`order` → producteur ; `payment`/`notification` → consommateurs+producteur)
- Spring Mail + Thymeleaf (`notification`, emails via `mail-dev`)
- MapStruct (mapping entité ↔ DTO), Lombok
- springdoc-openapi (Swagger sur `customer`/`product`/`order`)
- Micrometer + Prometheus (`/actuator/prometheus` sur les 8 services) + Grafana pour les dashboards
- Docker Compose pour l'infra (PostgreSQL, MongoDB, Kafka, mail-dev, Zipkin, Prometheus, Grafana)

## Documentation par service

- [services/discovery/README.md](services/discovery/README.md)
- [services/config-server/README.md](services/config-server/README.md)
- [services/customer/README.md](services/customer/README.md)
- [services/product/README.md](services/product/README.md)
- [services/order/README.md](services/order/README.md)
- [services/payment/README.md](services/payment/README.md)
- [services/notification/README.md](services/notification/README.md)
- [services/gateway/README.md](services/gateway/README.md)
