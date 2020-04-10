
# Table des matières

1.  [Présentation générale](#org4ca480a)
2.  [Questionnaire d’auto-évaluation](#orgff491da)
    1.  [Symptômes](#orgba285da)
    2.  [Facteur pronostique défavorable lié au terrain](#org54d4d6f)
    3.  [Autre question](#orgebff284)
    4.  [Définition des facteurs de gravité](#orgdc0f0d3)
    5.  [Calcul de l'indice de masse corporelle (IMC)](#org3957cc5)
3.  [Diagramme de l'arbre de décision](#org46ea24a)
4.  [Définition de l'arbre de décision](#orgdf85b42)
    1.  [Patient de moins de 15 ans](#orge125f96)
    2.  [Tout patient avec au moins un facteur de gravité majeur](#org1293546)
    3.  [Tout patient avec fièvre et toux](#org4774e5c)
    4.  [Tout patient avec fièvre ou (sans fièvre et avec (diarrhée ou (toux et douleurs) ou (toux et anosmie))](#org10959ca)
    5.  [Tout patient sans fièvre avec un seul symptôme parmi toux, douleurs, anosmie](#org77bd046)
    6.  [Tout patient sans fièvre ni aucun autre symptôme](#orgee6a2f9)



<a id="org4ca480a"></a>

# Présentation générale

Ce document présente le questionnaire d'auto-évaluation COVID19 et
l'algorithme traitant les réponses au questionnaire pour afficher une
orientation donnée au répondant (par exemple "Appelez le 15").

Les réponses au questionnaire permettent de calculer des variables
dont les valeurs finales vont ensuite déterminer l'orientation du
répondant.  Ces variables sont soit des variables booléennes (par
exemple : est ou n'est pas diabétique) soit des variables de comptage
(par exemple 0, 1 ou 2 facteurs de gravité majeures). Chaque question
met à jour une variable booléenne et, possiblement, incrémente une
variable de comptage.

L'algorithme se présente comme un arbre de décision qui doit être
parcouru de façon séquentielle et en fonction des valeurs finales de
toutes les variables, pour déterminer le message d'orientation à
afficher.

Version du document : `2020-04-06`

Ce document est [téléchargeable en version PDF](https://esante.gouv.fr/algorithme-orientation).

**Attention : cette documentation, le questionnaire et l'algorithme sont potentiellement modifiables après étude de cas et veille scientifique.**


<a id="orgff491da"></a>

# Questionnaire d’auto-évaluation


<a id="orgba285da"></a>

## Symptômes

-   *Pensez-vous avoir eu de la fièvre ces derniers jours (frissons, sueurs) ?* `OUI / NON / Je ne sais pas` (`Je ne sais pas > OUI`)
    -   *Quelle a été votre température la plus élevée ces dernières 48h ?*
        -   **< 35,5°C** => Facteur de gravité mineur
        -   35,5°C - 37,7°C
        -   37,8°C - 38,9°C
        -   **>= 39°C** => Facteur de gravité mineur
        -   Non renseignée & OUI à question précédente = fièvre présente.
-   *Avez-vous une toux ou une augmentation de votre toux habituelle ces derniers jours ?* `OUI / NON`
-   *Avez-vous noté une forte diminution de votre goût ou de votre odorat ces derniers jours ?* `OUI / NON`
-   *Avez-vous un mal de gorge ou des douleurs musculaires ou des courbatures inhabituelles ces derniers jours ?* `OUI / NON`
-   *Avez-vous de la diarrhée ces dernières 24 heures (au moins 3 selles molles) ?* `OUI / NON`
-   *Avez-vous une fatigue inhabituelle ces derniers jours ?* `OUI / NON`
    -   Si `OUI` : *Cette fatigue vous oblige-t-elle à vous reposer plus de la moitié de la journée ?* `OUI / NON (OUI => Facteur de gravité mineur`)
-   *Êtes-vous dans l'impossibilité de vous alimenter ou de boire DEPUIS 24 HEURES OU PLUS ?* `OUI / NON (OUI => Facteur de gravité majeur`)
-   *Dans les dernières 24 heures, avez-vous noté un manque de souffle INHABITUEL lorsque vous parlez ou faites un petit effort ?* `OUI / NON => Facteur de gravité majeur.`


<a id="org54d4d6f"></a>

## Facteur pronostique défavorable lié au terrain

-   *Quel est votre âge ?*
    -   < 15 ans
    -   >= 15 et < 50 ans
    -   >= 50 et < 70 ans
    -   **>= 70 ans**
-   *Quel est votre poids ? Quelle est votre taille ?*
    -   Indice de masse corporelle (IMC) < 30 kg/m²
    -   **Indice de masse corporelle (IMC) >= 30 kg/m²**
-   *Avez-vous de l’hypertension artérielle mal équilibrée ? Ou avez-vous une maladie cardiaque ou vasculaire ? Ou prenez-vous un traitement à visée cardiologique ?* **OUI** / NON / Je ne sais pas (OUI)
-   *Êtes-vous diabétique ?* **OUI** / NON
-   *Avez-vous ou avez-vous eu un cancer dans les 3 dernières années ?* **OUI** / NON
-   *Avez-vous une maladie respiratoire ? Ou êtes-vous suivi par un pneumologue ?* **OUI** / NON
-   *Avez-vous une insuffisance rénale chronique dialysée ?* **OUI** / NON
-   *Avez-vous une maladie chronique du foie ?* **OUI** / NON
-   *Êtes-vous enceinte ?* **OUI** / NON / Non applicable
-   *Avez-vous une maladie connue pour diminuer vos défenses immunitaires* **OUI** / NON / Je ne sais pas (NON)
-   *Prenez-vous un traitement immunosuppresseur ? C’est un traitement qui diminue vos défenses contre les infections.  Voici quelques exemples : corticoïdes, méthotrexate, ciclosporine, tacrolimus, azathioprine, cyclophosphamide (liste non exhaustive).* **OUI** / NON / Je ne sais pas (NON)

Le facteur pronostique est considéré positif s'il y a au moins 1 item **OUI** ou en gras (âge supérieur ou égal à 70 ans, indice de masse corporelle supérieur à 30 kg/m²).


<a id="orgebff284"></a>

## Autre question

-   *Quel est votre code postal ?*


<a id="orgdc0f0d3"></a>

## Définition des facteurs de gravité

-   Facteurs de gravité **mineurs** :
    -   Fièvre >= 39°C
    -   Fièvre < 35,5°C
    -   Fatigue : alitement > 50% du temps diurne

-   Facteurs de gravité **majeurs** :
    -   Gêne respiratoire
    -   Difficultés importantes pour s’alimenter ou boire depuis plus de 24 heures


<a id="org3957cc5"></a>

## Calcul de l'indice de masse corporelle (IMC)

L'indice de masse corporelle est égal au `POIDS` en kilogrammes divisé par le carré de la `TAILLE` en mètres.


<a id="org46ea24a"></a>

# Diagramme de l'arbre de décision

<a href="https://raw.githubusercontent.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/master/diagramme-algorithme-orientation-covid19.png"><img src="https://raw.githubusercontent.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/master/diagramme-algorithme-orientation-covid19.png" alg="Diagramme de l'arbre de décision pour l'algorithme d'orientation COVID 19" /></a>


<a id="orgdf85b42"></a>

# Définition de l'arbre de décision

Message à afficher pour tous : *Restez chez vous au maximum en attendant que les symptômes disparaissent. Prenez votre température deux fois par jour. Rappel des mesures d’hygiène. Un dispositif national grand public de soutien psychologique au bénéfice des personnes qui en auraient besoin est accessible via le numéro vert : 0 800 130 000.*


<a id="orge125f96"></a>

## Patient de moins de 15 ans

    Cette application n’est pas faite pour les personnes de moins de 15 ans.
    Prenez contact avec votre médecin généraliste au moindre doute.
    En cas d’urgence, appelez le 15.


<a id="org1293546"></a>

## Tout patient avec au moins un facteur de gravité majeur

`Appelez le 15.`


<a id="org4774e5c"></a>

## Tout patient avec fièvre et toux


### Tout patient sans facteur pronostique

    Votre situation peut relever d’un COVID 19. 
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile (SOS médecins, etc.)


### Tout patient avec un facteur pronostique ou plus


#### Si un ou deux facteurs de gravité mineurs

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile (SOS médecins, etc.)


#### Avec au moins deux facteurs de gravité mineurs

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    Si vous n'arrivez pas à obtenir de consultation, appelez le 15.


<a id="org10959ca"></a>

## Tout patient avec fièvre ou (sans fièvre et avec (diarrhée ou (toux et douleurs) ou (toux et anosmie))


### Tout patient sans facteur pronostique


#### Si pas de facteur de gravité mineur

    Votre situation peut relever d’un COVID 19 qu’il faut surveiller.
    Si de nouveaux symptômes apparaissent, refaites le test ou consultez votre médecin.
    Nous vous conseillons de rester à votre domicile.

Si moins de 50 ans :

    Votre situation peut relever d’un COVID 19 qu’il faut surveiller.
    Si de nouveaux symptômes apparaissent, refaites le test ou consultez votre médecin.
    Nous vous conseillons de rester à votre domicile.

Sinon :

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes pour vous alimenter ou boire apparaissent pendant plus de 24 heures.


#### Sinon (1 ou plusieurs facteurs de gravité mineurs)

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes pour vous alimenter ou boire apparaissent pendant plus de 24 heures.


### Tout patient avec un facteur pronostique ou plus


#### Si zéro ou un facteur de gravité mineur

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes pour vous alimenter ou boire apparaissent pendant plus de 24 heures.


#### Si au moins deux facteurs de gravité mineurs

    Votre situation peut relever d’un COVID 19.
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    Si vous n'arrivez pas à obtenir de consultation, appelez le 15.


<a id="org77bd046"></a>

## Tout patient sans fièvre avec un seul symptôme parmi toux, douleurs, anosmie


### Au moins un facteur pronostique

    Votre situation peut relever d’un COVID 19. Un avis médical est recommandé.
    Au moindre doute, appelez le 15. Nous vous conseillons de rester à votre domicile.


### Pas de facteur pronostique

    Votre situation peut relever d’un COVID 19 qu’il faut surveiller.
    Si de nouveaux symptômes apparaissent, refaites le test ou consultez votre médecin.
    Nous vous conseillons de rester à votre domicile.


<a id="orgee6a2f9"></a>

## Tout patient sans fièvre ni aucun autre symptôme

    Votre situation ne relève probablement pas du COVID 19.
    N’hésitez pas à contacter votre médecin en cas de doute.
    Vous pouvez refaire le test en cas de nouveau symptôme pour réévaluer la situation.
    Pour toute information concernant le COVID 19, composer le 0 800 130 000.

