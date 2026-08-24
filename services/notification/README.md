# notification-service

Microservice de notification : écoute `order-topic` et `payment-topic` (Kafka), enregistre une trace de chaque notification en MongoDB, et envoie un email de confirmation (via Thymeleaf pour le contenu HTML).

> ⚠️ Endpoints ouverts publiquement — pas de JWT/Keycloak, pas de rate limiting. Expose `/actuator/prometheus` (scrapé par Prometheus/Grafana) — a récupéré `spring-boot-starter-webmvc`+`actuator` pour ça, qu'il n'avait pas au départ. Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Spring Data MongoDB
- Spring Kafka (`spring-boot-starter-kafka`)
- Spring Mail + Thymeleaf (templates HTML dans `src/main/resources/templates/`)
- Spring Cloud Config (config-server)
- Spring MVC + Actuator + Micrometer/Prometheus — ajoutés uniquement pour exposer `/actuator/prometheus` (aucune route métier), ce service n'avait au départ aucun starter web
- Pas de client Eureka — ce service ne reçoit jamais d'appel entrant d'un autre service, pas besoin d'être découvrable

## Prérequis

| Service | Port | Rôle |
|---|---|---|
| `config-server` | 8888 | fournit `notification-service.yml` |
| MongoDB (Docker) | 27018 | base `notification-db` |
| Kafka (Docker) | 29092 (listener hôte) | consomme `order-topic` + `payment-topic` |
| `mail-dev` (Docker) | 1025 (SMTP), 1080 (UI web) | intercepte les emails en dev, rien n'est réellement envoyé |

⚠️ Le conteneur `mail-dev` peut apparaître "unhealthy" dans `docker ps` — c'est un faux positif (healthcheck mal configuré sur l'image), le serveur SMTP fonctionne normalement.

## Lancer le service

```bash
cd services/notification
mvn spring-boot:run
```

Port **8094**.

## Flux Kafka

```
order-service   --[order-topic]-->   notification-service --[email]--> mail-dev
payment-service --[payment-topic]--> notification-service --[email]--> mail-dev
```

Deux `@KafkaListener` dans `NotificationConsumer`, un par topic, groupe `notification-group`. Chaque message reçu :
1. Sauvegarde un document `Notification` en MongoDB (trace/historique).
2. Déclenche l'envoi d'un email (`@Async`) via `EmailService`, avec le template Thymeleaf correspondant (`order-confirmation.html` ou `payment-success.html`).

⚠️ **Désérialisation Kafka multi-topics** : ce service consomme deux types de messages différents (`OrderConfirmation`, `PaymentConfirmation`) depuis deux services producteurs distincts. Le header `__TypeId__` Kafka (nom de classe du côté producteur) doit être ignoré — `spring.json.use.type.headers: false` — sinon la désérialisation échoue et le message boucle en retry indéfiniment. Contrairement à `payment-service`, **pas** de `spring.json.value.default.type` unique ici (deux types possibles selon le topic) : Spring Kafka déduit le bon type directement depuis la signature de chaque méthode `@KafkaListener`.

## Voir les emails envoyés

Interface web MailDev : http://localhost:1080 — tous les emails "envoyés" en dev y apparaissent (rien ne part vers une vraie boîte mail).

## Config MongoDB (Spring Boot 4.1)

Le préfixe de propriété correct est `spring.mongodb.*`, **pas** `spring.data.mongodb.*` (ce dernier fonctionnait en Spring Boot 3, mais plus en 4.1 — l'auto-configuration a été déplacée dans un module séparé).
