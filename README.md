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
