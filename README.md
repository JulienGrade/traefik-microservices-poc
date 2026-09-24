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

### feat(order): identifier le service commandes derrière la gateway

Ajouter `/orders/instance`. `curl localhost:8082/orders/instance` retourne le nom du service et son hostname. Ce service restera à une instance tant que son stockage est en mémoire.

### build(shipping): créer le squelette Spring Boot de livraison

Créer séparément le troisième projet avec son test de contexte. `./mvnw -pl shipping-service -am test`. Il servira à constater l’ajout d’une route après démarrage de Traefik.

### build(shipping): activer le serveur HTTP de livraison

Ajouter Spring Web. `./mvnw -pl shipping-service spring-boot:run` : serveur sur 8083, sans route de devis à ce stade.

### feat(shipping): exposer un devis de livraison

Ajouter `GET /shipping/quote` avec montant 4.90 EUR et délai trois jours. Après lancement : `curl localhost:8083/shipping/quote`.

### test(shipping): vérifier le contrat du devis

Tester le statut 200 et la réponse du devis sur un vrai serveur Spring Boot. `./mvnw -pl shipping-service -am test`.

### feat(shipping): exposer la santé du service de livraison

Ajouter `/actuator/health` pour les contrôles Docker et Traefik. `./mvnw verify` valide désormais les trois services.

### build(docker): exclure les fichiers inutiles des contextes de build

Exclure les sorties Maven, les métadonnées Git et les réglages locaux. Les prochains builds Docker ne copieront que les sources utiles.

### build(product): conteneuriser le service avec un runtime non root

Ajouter le build Maven multi-stage de product-service, tests inclus, puis le JAR dans une image JRE. `docker build -f product-service/Dockerfile -t poc-product .`. Aucun serveur Maven n’est conservé dans le runtime.

### build(order): conteneuriser le service avec un runtime non root

Ajouter le build Maven multi-stage de order-service, tests inclus, puis le JAR dans une image JRE. `docker build -f order-service/Dockerfile -t poc-order .`. Aucun serveur Maven n’est conservé dans le runtime.

### build(shipping): conteneuriser le service avec un runtime non root

Ajouter le build Maven multi-stage de shipping-service, tests inclus, puis le JAR dans une image JRE. `docker build -f shipping-service/Dockerfile -t poc-shipping .`. Aucun serveur Maven n’est conservé dans le runtime.

### build(compose): relier catalogue et commandes sur le réseau Docker

Définir les deux services principaux et leur réseau partagé. `docker compose up --build -d`, puis `curl localhost:8081/products`. Les commandes appellent le catalogue par le DNS Docker ; aucun nom de conteneur fixe.

### fix(product): contrôler la disponibilité HTTP du conteneur

Ajouter curl et le HEALTHCHECK Actuator dans l’image product-service. Vérifier après reconstruction avec `docker compose ps` ; le healthcheck de shipping sera utilisé lors de son activation optionnelle.

### fix(order): contrôler la disponibilité HTTP du conteneur

Ajouter curl et le HEALTHCHECK Actuator dans l’image order-service. Attendre un catalogue healthy avant de démarrer les commandes. Vérifier après reconstruction avec `docker compose ps` ; le healthcheck de shipping sera utilisé lors de son activation optionnelle.

### fix(shipping): contrôler la disponibilité HTTP du conteneur

Ajouter curl et le HEALTHCHECK Actuator dans l’image shipping-service. Vérifier après reconstruction avec `docker compose ps` ; le healthcheck de shipping sera utilisé lors de son activation optionnelle.

### feat(traefik): découvrir et router le catalogue par labels Docker

Ajouter le provider Docker avec exposition désactivée par défaut, déclarer la route catalogue et retirer son port hôte. `docker compose up --build -d --wait --wait-timeout 180`, puis `curl localhost:8080/api/products` : 200. Les labels remplacent la liste centrale d’adresses.

### feat(traefik): router les commandes et retirer leur port direct

Exposer les commandes via `/api/orders` sur la même entrée 8080 et retirer 8082 de l’hôte. Après `docker compose up -d`, tester la création de commande à travers la gateway. `/api/orders/instance` identifie le service atteint.

### feat(traefik): exposer le dashboard sur une entrée locale distincte

Après `docker compose up -d`, ouvrir `http://localhost:8088/dashboard/` avec le slash final. Le dashboard est séparé de l’entrée API ; `curl -i localhost:8080/api/http/routers` reste en 404. Usage local sans authentification.

### feat(traefik): journaliser les requêtes au format JSON

Activer les access logs. Après `docker compose up -d` et un appel API : `docker compose logs traefik` permet de retrouver route, service, statut et durée de la requête.

### feat(traefik): contrôler activement la santé des serveurs

Ajouter les healthchecks Traefik sur les deux services. Après `docker compose up -d`, consulter leur état dans le dashboard. Cette surveillance traite le trafic gateway ; elle est indépendante des healthchecks Docker et de l’appel interne order → product.

### feat(traefik): partager et recharger les middlewares par fichier

Déplacer stripPrefix dans une chaîne commune et ajouter les en-têtes X-Gateway et nosniff. Après `docker compose up -d`, `curl -i localhost:8080/api/products` doit afficher ces deux en-têtes. Les futurs changements du fichier sont surveillés sans redémarrage de Traefik.

### feat(traefik): compresser les réponses compatibles

Ajouter le middleware compress à la chaîne commune. Vérifier `curl -I -H "Accept-Encoding: gzip" localhost:8080/api/products` ; une petite réponse peut rester non compressée sous le seuil du middleware.

### feat(traefik): limiter le débit du catalogue

Appliquer un quota de 20 requêtes/s avec burst 40, uniquement sur le catalogue. Après `docker compose up -d`, une rafale doit produire des 429, puis les appels reviennent à 200 après recharge. Le quota est par IP et local à cette gateway.

### feat(discovery): découvrir le service optionnel de livraison

Avant activation, `/api/shipping/quote` renvoie 404. Lancer `docker compose --profile discovery up --build -d --wait shipping-service`, puis vérifier 200. Aucun changement de configuration Traefik ni de ses routes existantes. Arrêter avec `docker compose --profile discovery stop shipping-service` : après convergence, la route revient à 404.

### build(test): créer le module Java de tests de la stack

Ajouter un module Maven activé uniquement par `-Pe2e`, JUnit et Failsafe, avec client HTTP Java et commandes Docker bornées. Compiler sans exécuter de scénario : `./mvnw -Pe2e -pl integration-tests test-compile`. Aucun autre runtime de test n’est nécessaire.

### test(api): vérifier le parcours métier à travers Traefik en Java

Avec la stack démarrée : `./mvnw -Pe2e -pl integration-tests verify -Dit.test=ApiContractsIT`. Vérifier catalogue, prix exact, création et relecture d’une commande, validation et produit absent.

### test(routing): contrôler les frontières des routes exposées

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=RoutingIsolationIT`. Vérifier que les préfixes ressemblants, Actuator et l’API du dashboard ne sont pas exposés sur l’entrée métier.

### test(middleware): vérifier les en-têtes communs sur les deux services

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=ResponseHeadersIT`. Vérifier la chaîne de middlewares commune, indépendamment du service cible.

### test(rate-limit): vérifier le refus des rafales et la recharge du quota

Exécuter sans autre charge `./mvnw -Pe2e -pl integration-tests verify -Dit.test=RateLimitIT`. La rafale doit produire des 429, puis revenir à 200 après recharge ; ce test ne constitue pas un benchmark.

### test(reload): contrôler le rechargement du fichier sans recréer Traefik

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=ReloadIT`. Le test change temporairement l’en-tête, attend son application et restaure le fichier dans un finally. Il contrôle que l’identifiant du conteneur Traefik ne change pas ; éviter toute édition simultanée du fichier.

### test(discovery): contrôler ajout et retrait automatiques du service optionnel

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=DiscoveryIT`. Le test observe 404 → 200 → 404, avec le même conteneur Traefik. Il laisse shipping-service arrêté et construit son image au besoin.

### test(scaling): observer trois réplicas sans changer la gateway

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=ScalingIT`. Le test exige trois hostnames distincts et le même identifiant Traefik. Les requêtes sont espacées pour rester sous le quota. Il rétablit une instance catalogue à la fin.

### test(failover): contrôler le retrait d’une instance arrêtée

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=FailoverIT`. Arrêter une des deux instances et contrôler vingt réponses sur la survivante après six secondes de convergence. Le test rétablit une instance ; il ne garantit pas zéro erreur durant la détection de panne.

### test(outage): vérifier le 503 lors d’une panne complète du catalogue

Exécuter `./mvnw -Pe2e -pl integration-tests verify -Dit.test=CatalogOutageIT`. Le test arrête le catalogue, vérifie le 503 sur la création de commande et rétablit le service. Il distingue la gestion de dépendance Spring Boot du retrait de serveurs par Traefik.
