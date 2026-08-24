# discovery

Serveur Eureka — annuaire de services pour toute la plateforme. Chaque microservice s'y enregistre au démarrage et l'interroge pour localiser les autres services (utilisé par Spring Cloud LoadBalancer / OpenFeign).

> ⚠️ Pas de JWT/Keycloak, pas de rate limiting, pas d'Actuator ici, pas de Grafana/Prometheus. Détails dans le [README racine](../../README.md#️-limitations--hors-scope-de-ce-projet).

## Stack

- Java 25, Spring Boot 4.1.0, Spring Cloud 2025.1.2
- Netflix Eureka Server

## Lancer le service

C'est le **premier** service à démarrer, avant tous les autres (y compris `config-server`, qui s'y enregistre aussi).

```bash
cd services/discovery
mvn spring-boot:run
```

Démarre sur le port **8761** (configuré localement, pas de config-server nécessaire pour lui puisqu'il ne consomme pas sa propre config via `config-server` — il fournit la config Eureka lui-même).

## Dashboard

Une fois démarré : http://localhost:8761

Le dashboard liste toutes les instances de services actuellement enregistrées (`CUSTOMER-SERVICE`, `PRODUCT-SERVICE`, `ORDER-SERVICE`, `CONFIG-SERVER`...). Utile pour vérifier rapidement qu'un service s'est bien enregistré après son démarrage.

## Problème fréquent

`Port 8761 was already in use` → une instance précédente tourne encore (souvent un doublon lancé depuis IntelliJ sans avoir arrêté la précédente). Vérifier dans le panneau **Services** d'IntelliJ, et activer **"Single instance only"** sur la Run Configuration pour éviter que ça se reproduise.
