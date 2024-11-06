# Projet Génie Logiciel, Ensimag.
gl42, 01/01/2024

ahjaous, bouihih, guessouo, senameg, thiongam

## Exécution des tests

Les fichiers de tests Deca sont dans le répertoire `src/test/deca`.

Les scripts de tests sont dans le répertoire `src/test/script`.

Le fichier `src/test/script/tests.py` permet de lancer nos tests sur les fichiers Deca. Il s'exécute avec la commande `python3 tests.py [options]`. Les options sont les suivantes :
- sans option : exécute tous les tests.
- `-h` : affiche l'aide.
- `-w` : redirige les résultats dans des fichiers `.lis` et `.res` au lieu de les afficher dans la sortie standard.
- `-dev` : exécute les tests en mode développement.
- `-lex` : exécute les tests pour l'analyse lexicographique.
- `-synt` : exécute les tests pour l'analyse syntaxique.
- `-context` : exécute les tests pour les vérifications contextuelles.
- `-gen` : exécute les tests pour la génération de code.
- `-decompile` : exécute les tests pour la décompilation.

La commande `mvn test` permet de lancer les scripts Shell et le script Python sans option.

La commande `make verify` permet de recompiler le projet et de lancer les scripts Shell et le script Python sans option.

## Exécution du compilateur

Le compilateur Deca se lance avec la commande `deca [options] file.deca`. Les options sont les suivantes :

- `-b (banner)` : affiche une bannière indiquant le nom de l'équipe

- `-p (parse)` : arrête decac après l'étape de construction de l'arbre, et affiche la décompilation de ce dernier (i.e. s'il n'y a qu'un fichier source à compiler, la sortie doit être un programme deca syntaxiquement correct)

- `-v (verification)` : arrête decac après l'étape de vérifications (ne produit aucune sortie en l'absence d'erreur)

- `-n (no check)` : supprime les tests à l'exécution spécifiés dans les points 11.1 et 11.3 de la sémantique de Deca.

- `-r X (registers)` : limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16

- `-d (debug)` : active les traces de debug. Répéter l'option plusieurs fois pour avoir plus de traces.

- `-P (parallel)` : s'il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation) ; ne pas utiliser avec l'option -v

