# Programme de vérification formelle de l'algorithme d'orientation Covid-19

## Installation

Cet outil nécessite Python >= 3.6 et le paquet pip `z3-solver`.

```
pip3 install z3-solver
```

## Vérification

Pour lancer la vérification, il suffit de lancer :

```
python covid.py
```

Si cela affiche "Pas de non-terminaison possible!", c'est bon : il
existe une preuve mathématique que quelles que soient les réponses au
questionnaire, l'algorithme renverra toujours vers une fin prédéfinie.

Si non, cela affiche les réponses aux questionnaires qui déclenchent
une faille dans l'algorithme.

Cet outil s'appuie sur [le pseudo code de référence](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#arbre-de-d%C3%A9cision). 

Si ce pseudo code est mis à jour, le code Python qui appelle Z3 doit
l'être aussi pour refléter le nouvel arbre de décision.

## Fonctionnement de la vérification

Le problème est modélisé grâce au [solveur z3](https://github.com/Z3Prover/z3).

À partir des différents paramètres d'entrée (age, présence de fièvre…), on calcule la sortie de l'algorithme.
Cette sortie est modélisée comme un tableau de 8 booleens, autant qu'il y a de cas de terminaisons. La `i-ème` valeur du tableau est à `True` lorsque l'algorithme décide que, pour le jeu de paramètres fournis, il oriente vers la terminaison `i`.

 - on déclare les variables du problème
 - on ajoute des contraintes (nombre entier positif, relations entre les variables…)
 - on modélise les [règles de l'arbre de décision](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#arbre-de-d%C3%A9cision): les différentes conditions logiques permettent d'arriver à des terminaisons. C'est la fonction `algo_6e5e17a`, où `6e5e17a` indique la version de l'algorithme qui est modélisé.

`z3` se charge alors de vérifier:

 - que chaque fin peut être atteinte
 - que chaque jeu de paramètres d'entrée aboutit bien à un état final
