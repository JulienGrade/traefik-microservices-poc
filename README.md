# Traefik Microservices POC

## Lancer

Les commandes de lancement sont introduites dans les étapes ci-dessous.

## Tester

Chaque étape indique la commande adaptée aux éléments disponibles.

## Étapes et commits

Les titres sont les messages exacts des commits, dans leur ordre de création. Utiliser `git log --oneline --reverse`, puis `git switch --detach <sha>` pour consulter une étape. Revenir avec `git switch main`.

Auteur : `JulienGrade <JulienGrade@users.noreply.github.com>`.

### chore(repo): initialiser le dépôt et les exclusions Git

Créer la branche main et le README. Contrôler `git status` et `git log -1 --format=fuller`.

### build(maven): créer le parent multi-module Spring Boot

Définir Spring Boot 3.5.16, Java 17 et les coordonnées communes. Avec Maven installé : `mvn validate`. Aucun service n’est encore créé.

### build(maven): ajouter le wrapper Maven 3.9.11

Ajouter les lanceurs Unix et Windows. Vérifier `./mvnw --version` ; Windows : `.\mvnw.cmd --version`. Toutes les commandes suivantes partent de la racine du dépôt.

### build(product): créer le squelette Spring Boot du catalogue

Créer le projet avec POM, classe de démarrage, propriétés et test de contexte. `./mvnw -pl product-service -am test`. Le squelette n’expose pas encore de serveur HTTP.

### build(product): activer le serveur HTTP et la sérialisation JSON

Ajouter Spring Web. `./mvnw -pl product-service spring-boot:run` : le serveur écoute sur 8081 ; les routes métier restent à ajouter.

### feat(product): définir le modèle du produit

Définir le contrat produit : identifiant, nom et prix BigDecimal. Vérifier `./mvnw -pl product-service -am test`.

### feat(product): initialiser le catalogue en mémoire

Ajouter trois produits fixes et le service de consultation du catalogue, avec test du prix exact. `./mvnw -pl product-service -am test`.

### feat(product): exposer la liste des produits

Ajouter `GET /products` et l’injection du catalogue dans le contrôleur. Démarrer product-service, puis `curl -i localhost:8081/products` : 200 et trois produits.

### feat(product): consulter un produit et retourner 404 si absent

Ajouter `GET /products/{id}` et la recherche dans le service. `curl -i localhost:8081/products/1` : 200 ; `/products/999` : 404.
