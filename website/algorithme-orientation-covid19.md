
# Table des matières

-   [1 Présentation générale](#org412ae5e)
-   [2 Questionnaire d’auto-évaluation](#orga048a96)
    -   [2.1 Symptômes](#org359eaa8)
    -   [2.2 Facteur pronostique défavorable lié au terrain](#org9fb7409)
    -   [2.3 Autre question](#org6e49b22)
    -   [2.4 Définition des facteurs de gravité](#org9036ae6)
    -   [2.5 Calcul de l'indice de masse corporelle (IMC)](#org91fdeec)
-   [3 Définition de l'arbre de décision](#orgcd2fcd3)
    -   [3.1 Patient de moins de 15 ans](#org9fd74d4)
    -   [3.2 Tout patient avec au moins un facteur de gravité majeur](#org7ef0344)
    -   [3.3 Tout patient avec fièvre et toux](#org4badc89)
    -   [3.4 Tout patient avec fièvre ou (sans fièvre et avec (diarrhée ou (toux et douleurs) ou (toux et anosmie) ou (douleurs et anosmie)))](#org8f15234)
    -   [3.5 Tout patient sans fièvre avec un seul symptôme parmi toux, douleurs, anosmie](#orgfa3d40b)
    -   [3.6 Tout patient sans fièvre ni aucun autre symptôme](#orgb06a919)
-   [4 Diagramme de l'arbre de décision](#org0ac1a4a)



<a id="org412ae5e"></a>

# 1 Présentation générale

Ce document présente le questionnaire d'auto-évaluation Covid-19 et
l'algorithme traitant les réponses au questionnaire pour afficher une
orientation donnée au répondant (par exemple "Appelez le 15").

Les réponses au questionnaire permettent de calculer des variables
dont les valeurs finales vont ensuite déterminer l'orientation du
répondant.  Ces variables sont soit des variables booléennes (par
exemple : le répondant est ou n'est pas diabétique) soit des variables
de comptage (par exemple les facteurs de gravité et le facteur
pronostique défavorable), soit des variables continues (l'indice de
masse corporelle (IMC), un nombre décimal).  Chaque réponse met à jour
une variable booléenne et, possiblement, incrémente une variable de
comptage ou calcule l'IMC.

L'algorithme se présente comme un arbre de décision qui doit être
parcouru de façon séquentielle et en fonction des valeurs finales de
toutes les variables, pour déterminer le message d'orientation à
afficher.

Version du document : `2020-06-09`

Ce document est [téléchargeable en version PDF](https://esante.gouv.fr/algorithme-orientation).

**Attention : cette documentation, le questionnaire et l'algorithme sont potentiellement modifiables après étude de cas et veille scientifique.**


<a id="orga048a96"></a>

# 2 Questionnaire d’auto-évaluation


<a id="org359eaa8"></a>

## 2.1 Symptômes

-   *Quelle a été votre température la plus élevée ces dernières 48 heures ?*
    -   **< 35,5°C** `-> Facteur de gravité mineur`
    -   35,5°C - 37,7°C
    -   37,8°C - 38,9°C
    -   **>= 39°C** `-> Facteur de gravité mineur`
    -   **Je ne sais pas** `-> Facteur de gravité mineur`
-   *Avez-vous une toux ou votre toux habituelle s’est-elle modifiée ces derniers jours ?* `OUI / NON`
-   *Avez-vous noté une perte ou une forte diminution de votre goût ou de votre odorat ces derniers jours ?* `OUI / NON`
-   *Avez-vous un mal de gorge ou des douleurs musculaires ou des courbatures inhabituelles ou des maux de tête inhabituels ces derniers jours ?* `OUI / NON`
    -   Si `OUI`, afficher le message : *Si vous avez des maux de tête qui ne disparaissent pas après la prise de médicaments anti-douleurs (dont paracétamol) et que vous décririez comme intolérables : Appelez le 15.*
-   *Avez-vous de la diarrhée ces dernières 24 heures (au moins 3 selles molles) ?* `OUI / NON`
-   *Avez-vous une fatigue inhabituelle ces derniers jours ?* `OUI / NON`
    -   Si `OUI` : *Cette fatigue vous oblige-t-elle à vous reposer plus de la moitié de la journée ?* `OUI / NON (OUI -> Facteur de gravité mineur)`
-   *Êtes-vous dans l'impossibilité de vous alimenter ou de boire DEPUIS 24 HEURES OU PLUS ?* `OUI / NON (OUI => Facteur de gravité majeur)`
-   *Dans les dernières 24 heures, avez-vous noté un manque de souffle INHABITUEL lorsque vous parlez ou faites un petit effort ?* `OUI / NON (OUI -> Facteur de gravité majeur)`


<a id="org9fb7409"></a>

## 2.2 Facteur pronostique défavorable lié au terrain

-   *Quel est votre âge ?*
    -   < 15 ans
    -   >= 15 et < 50 ans
    -   >= 50 et < 65 ans
    -   **>= 65 ans**
-   *Quel est votre poids ? Quelle est votre taille ?*
    -   Indice de masse corporelle (IMC) < 30 kg/m²
    -   **Indice de masse corporelle (IMC) >= 30 kg/m²**
-   *Avez-vous des antécédents de maladie cardiovasculaire : hypertension artérielle compliquée (avec complications cardiaques, rénales), accident vasculaire cérébral, maladie coronaire (infarctus), chirurgie cardiaque, insuffisance cardiaque avec essoufflement au moindre effort ?* **OUI** / NON / Je ne sais pas (OUI)
-   *Avez-vous un diabète mal équilibré ou avec des complications (yeux, reins) ?* **OUI** / NON
-   *Avez-vous un cancer évolutif sous traitement (hors hormonothérapie) ?* **OUI** / NON
-   *Avez-vous une maladie respiratoire chronique (bronchopneumopathie obstructive, asthme sévère, fibrose pulmonaire, syndrome d’apnées du sommeil, mucoviscidose) ou êtes-vous suivi par un pneumologue ?* **OUI** / NON
-   *Avez-vous une insuffisance rénale chronique avec besoin de faire de la dialyse ?* **OUI** / NON
-   *Avez-vous une cirrhose ?* **OUI** / NON
-   *Êtes-vous enceinte au 3ème trimestre de votre grossesse ?* **OUI** / NON / Non applicable
-   *Avez-vous une drépanocytose homozygote (forme majeure) ou avez-vous bénéficié d’une splénectomie (ablation de la rate) à cause de la drépanocytose ?* **OUI** / NON
-   *Avez-vous une immunodépression, par exemple : médicamenteuse (chimiothérapie anti cancéreuse, traitement immunosuppresseur, biothérapie et/ou corticothérapie à dose immunosuppressive depuis plus de 15 jours) ; infection à VIH non contrôlée ou avec des CD4 <200/mm3 ; consécutive à une greffe d'organe solide ou de cellules souches hématopoïétiques ; liée à une hémopathie maligne en cours de traitement ?* **OUI** / NON / Je ne sais pas (NON)

Le facteur pronostique est considéré positif s'il y a au moins 1 item **OUI** ou en gras (âge supérieur ou égal à 65 ans, indice de masse corporelle supérieur à 30 kg/m²).


<a id="org6e49b22"></a>

## 2.3 Autre question

-   *Quel est le code postal de votre lieu de résidence actuel ?*


<a id="org9036ae6"></a>

## 2.4 Définition des facteurs de gravité

-   Facteurs de gravité **mineurs** :
    -   Fièvre >= 39°C ou < 35,5°C ou "Je ne sais pas"
    -   Fatigue : alitement > 50% du temps diurne

-   Facteurs de gravité **majeurs** :
    -   Gêne respiratoire
    -   Difficultés importantes pour s’alimenter ou boire depuis plus de 24 heures


<a id="org91fdeec"></a>

## 2.5 Calcul de l'indice de masse corporelle (IMC)

L'indice de masse corporelle est égal au `POIDS` en kilogrammes divisé par le carré de la `TAILLE` en mètres.


<a id="orgcd2fcd3"></a>

# 3 Définition de l'arbre de décision

Message à afficher pour tous : *Restez chez vous au maximum en attendant que les symptômes disparaissent. Prenez votre température deux fois par jour. Rappel des mesures d’hygiène. Un dispositif national grand public de soutien psychologique au bénéfice des personnes qui en auraient besoin est accessible via le numéro vert : 0 800 130 000.*


<a id="org9fd74d4"></a>

## 3.1 Patient de moins de 15 ans

    Cette application n’est pas faite pour les personnes de moins de 15 ans.
    Prenez contact avec votre médecin généraliste au moindre doute.
    
    En cas d’urgence, appelez le 15.

Ce message correspond à `FIN1 (less_15)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="org7ef0344"></a>

## 3.2 Tout patient avec au moins un facteur de gravité majeur

`Appelez le 15.`

Ce message correspond à `FIN5 (SAMU)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="org4badc89"></a>

## 3.3 Tout patient avec fièvre et toux


### Tout patient sans facteur pronostique

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile (SOS médecins, etc.)
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN6 (consultation_surveillance_3)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


### Tout patient avec un facteur pronostique ou plus


#### Si un facteur de gravité mineur

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste. Au moindre
    doute, appelez le 15.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN7 (consultation_surveillance_4)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


#### Avec au moins deux facteurs de gravité mineurs

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile.  Si vous n'arrivez pas à obtenir de consultation, appelez
    le 15.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN4 (consultation_surveillance_2)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="org8f15234"></a>

## 3.4 Tout patient avec fièvre ou (sans fièvre et avec (diarrhée ou (toux et douleurs) ou (toux et anosmie) ou (douleurs et anosmie)))


### Tout patient sans facteur pronostique


#### Si pas de facteur de gravité mineur et moins de 50 ans

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile (SOS médecins, etc.)
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN6 (consultation_surveillance_3)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


#### Si pas de facteur de gravité mineur et 50 ans ou plus

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite à domicile.
    
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes
    pour vous alimenter ou boire apparaissent pendant plus de 24 heures.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN3 (consultation_surveillance_1)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


#### Si 1 ou plusieurs facteurs de gravité mineurs

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile.
    
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes
    pour vous alimenter ou boire apparaissent pendant plus de 24 heures.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN3 (consultation_surveillance_1)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


### Tout patient avec un facteur pronostique ou plus


#### Si zéro ou un facteur de gravité mineur

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile.
    
    Appelez le 15 si une gêne respiratoire ou des difficultés importantes
    pour vous alimenter ou boire apparaissent pendant plus de 24 heures.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN3 (consultation_surveillance_1)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


#### Si au moins deux facteurs de gravité mineurs

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile.
    
    Si vous n'arrivez pas à obtenir de consultation, appelez le 15.
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN4 (consultation_surveillance_2)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="orgfa3d40b"></a>

## 3.5 Tout patient sans fièvre avec un seul symptôme parmi toux, douleurs, anosmie


### Au moins un facteur pronostique

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste.
    
    Au moindre doute, appelez le 15. En attendant et pour casser les
    chaînes de transmission, nous vous conseillons de vous isoler et de
    respecter les gestes barrières pour protéger vos proches.

Ce message correspond à `FIN7 (consultation_surveillance_4)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


### Pas de facteur pronostique

    Votre situation peut relever d’un Covid-19.
    
    Demandez une téléconsultation ou un médecin généraliste ou une visite
    à domicile (SOS médecins, etc.)
    
    En attendant et pour casser les chaînes de transmission, nous vous
    conseillons de vous isoler et de respecter les gestes barrières pour
    protéger vos proches.

Ce message correspond à `FIN6 (consultation_surveillance_3)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="orgb06a919"></a>

## 3.6 Tout patient sans fièvre ni aucun autre symptôme

    Votre situation ne relève probablement pas du Covid-19.
    
    N’hésitez pas à contacter votre médecin en cas de doute.
    
    Vous pouvez refaire le test en cas de nouveau symptôme pour réévaluer
    la situation.
    
    Pour toute information concernant le Covid-19, composer le 0 800 130 000.

Ce message correspond à `FIN8 (surveillance)` dans la [documentation détaillée](https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/blob/master/pseudo-code.org#messages-dorientation-possibles).


<a id="org0ac1a4a"></a>

# 4 Diagramme de l'arbre de décision

<a href="https://raw.githubusercontent.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/master/diagramme-algorithme-orientation-covid19.png"><img src="https://raw.githubusercontent.com/Delegation-numerique-en-sante/covid19-algorithme-orientation/master/diagramme-algorithme-orientation-covid19.png" alg="Diagramme de l'arbre de décision pour l'algorithme d'orientation Covid-19" /></a>

