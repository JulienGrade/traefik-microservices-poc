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

### test(product): vérifier les contrats HTTP du catalogue

Tester la liste, le prix exact et le produit absent avec un vrai serveur Spring Boot sur port aléatoire. `./mvnw -pl product-service -am test`.

### feat(product): exposer la santé du catalogue

Ajouter Actuator, exposer uniquement health/info et activer Problem Details. Après lancement : `curl localhost:8081/actuator/health` retourne UP.

### feat(product): identifier l’instance qui répond aux requêtes

Ajouter `/products/instance`, alimenté par HOSTNAME, pour observer les réplicas. `curl localhost:8081/products/instance`. Tester : `./mvnw -pl product-service -am test`.

### build(order): créer le squelette Spring Boot des commandes

Créer séparément le deuxième projet, son POM, sa classe de démarrage et son test de contexte. `./mvnw -pl order-service -am test`.

### build(order): activer le serveur HTTP des commandes

Ajouter Spring Web. `./mvnw -pl order-service spring-boot:run` démarre sur 8082, sans route métier à ce stade.

### build(order): ajouter la validation des requêtes

Ajouter Jakarta Validation via le starter Spring Boot. `./mvnw -pl order-service -am test`. Les contraintes métier seront définies dans le contrat de création.

### feat(order): définir le contrat de réponse du catalogue

Définir le DTO reçu du catalogue dans le service commandes, sans dépendance au module product-service. Vérifier `./mvnw -pl order-service -am test`.

### feat(order): appeler le catalogue avec RestClient

Créer le client HTTP configurable par CATALOG_URL. L’appel interservices est direct ; aucune passerelle n’intervient entre les deux services. Vérifier `./mvnw -pl order-service -am test`.

### fix(order): borner les délais et traduire les pannes du catalogue

Limiter connexion et lecture à deux secondes ; traduire produit absent en 404, panne distante en 503 et réponse incohérente en 502. Vérifier `./mvnw -pl order-service -am test`.

### test(order): couvrir les réponses du client catalogue

Tester le vrai RestClient contre un serveur HTTP de test : succès, 404, erreur serveur et connexion refusée. `./mvnw -pl order-service -am test`.

### feat(order): définir et valider la demande de commande

Exiger un identifiant de produit positif et une quantité comprise entre 1 et 100. `./mvnw -pl order-service -am test`.

### feat(order): définir le modèle de commande

Ajouter UUID, produit, quantité, prix unitaire, total et date de création. Utiliser BigDecimal pour les montants. `./mvnw -pl order-service -am test`.

### feat(order): créer une commande au prix du catalogue

Calculer le montant côté serveur et enregistrer la commande dans une map concurrente. Ajouter le test de calcul métier. `./mvnw -pl order-service -am test`.

### feat(order): retrouver une commande par son identifiant

Ajouter la recherche d’une commande enregistrée et le 404 si elle est absente. Le stockage est local au processus et disparaît au redémarrage. `./mvnw -pl order-service -am test`.

### feat(order): exposer la création HTTP d’une commande

Ajouter `POST /orders`, validation, statut 201 et Location. Avec les deux services lancés : `curl -i localhost:8082/orders -H "Content-Type: application/json" -d '{"productId":1,"quantity":2}'` ; total attendu 179.80.

### feat(order): exposer la consultation HTTP d’une commande

Ajouter `GET /orders/{uuid}`. Réutiliser un identifiant reçu lors de la création ; attendre 200. Un UUID inexistant renvoie 404.

### test(order): vérifier création lecture et quantités invalides

Tester HTTP 201, Location, prix serveur, relecture, quantités invalides et commande absente. `./mvnw -pl order-service -am test`.

### feat(order): exposer la santé du service commandes

Ajouter Actuator et Problem Details. `curl localhost:8082/actuator/health` : UP après lancement. Les détails de santé ne sont pas exposés.
