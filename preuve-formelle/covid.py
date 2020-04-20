# coding=utf8

from z3 import *

# D'abord, il faut déclarer créer une instance du solveur Z3
s = Solver()

# On déclare toutes les entrées du problèmes, booléennes ou entières
age = Int("age")
fievre = Bool("fievre")
toux = Bool("toux")
douleurs = Bool("douleurs")
anosmie = Bool("anosmie")
diarrhee = Bool("diarrhee")
facteurs_pronostiques = Int("facteurs_pronostiques")
facteurs_gravite_mineur = Int("facteurs_gravite_mineur")
facteurs_gravite_majeur = Int("facteurs_gravite_majeur")

# Le solveur Z3 fonctionne en raisonnant sur un ensemble de contraintes portant
# sur les variables du problèmes. On ajoute des contraintes avec s.add()
# Ici, les contraintes sur les entrées.
s.add(age >= 0)
s.add(facteurs_pronostiques >= 0)
s.add(facteurs_gravite_mineur >= 0)
s.add(facteurs_gravite_majeur >= 0)

# Contraintes sur la définition des facteurs gravité mineurs : la fièvre en
# fait partie.
s.add(Implies(fievre, facteurs_gravite_mineur > 0))
s.add(facteurs_gravite_mineur <= 2)
s.add(Implies(facteurs_gravite_mineur == 2, fievre))

# Liste des variables du problème
variables = [age, fievre, toux, douleurs, anosmie, diarrhee,
             facteurs_pronostiques, facteurs_gravite_majeur, facteurs_gravite_mineur]


# La fonction suivante nous permet d'encoder comme une contrainte le fait
# de déclarer une condition de terminaison de l'algorithme.
# Ici "cond" est la condition à laquelle on veut que l'algorithme termine, et
# "number" le numéro du cas de terminaison.
def implies_done_base(cond, number, done, implies_done_acc):
    # L'algorithme est séquentiel : lorsque l'on atteint une condition de
    # terminaison, on arrête de l'exécuter. Or Z3 n'est pas séquentiel, les
    # contraintes ne fonctionnent pas comme ça. On a donc besoin ici d'un
    # accumulateur des conditions de terminaisons déjà rencontrées afin
    # d'émuler ce comportement séquentiel pour Z3.
    # On termine dans ce cas si la condition est remplie et que l'on est pas
    # dans un des cas de terminaison précédent.
    cond_acced = And(cond, Not(implies_done_acc))
    # On peut définit plusieurs conditions qui mènent au même cas de terminaison,
    # il est donc nécessaire de faire un "ou" logique avec le contenu de la
    # case de la liste.
    done[number - 1] = Or(done[number - 1], cond_acced)
    # On met à jour l'accumulateur
    return Or(cond, implies_done_acc)

# Encodage du pseudo-code dans Z3

DONE_LENGTH = 8

def algo_6e5e17a():
    # VERSION 6e5e17a

    # L'accumulateur est initialisé à False car c'est une série de ou logiques.
    implies_done_acc = BoolVal(False)
    # L'algorithme définit DONE_LENGTH cas de terminaison que l'on stockera dans cette liste
    # comme autant de booléens Z3.
    done = [BoolVal(False) for i in range(1, DONE_LENGTH + 1)]

    def implies_done(cond, number):
        nonlocal implies_done_acc
        implies_done_acc = implies_done_base(
            cond, number, done, implies_done_acc)

    # SI moins de 15 ans => FIN1
    implies_done(age < 15, 1)
    #
    # SI >= 1 facteurs de gravité majeurs => FIN5
    implies_done(facteurs_gravite_majeur >= 1, 5)
    #
    # SI fièvre ET toux
    cond1 = And(fievre, toux)
    #    SI 0 facteur pronostique => FIN6
    implies_done(And(cond1, facteurs_pronostiques == 0), 6)
    #    SI >= 1 facteurs pronostiques
    cond2 = (facteurs_pronostiques >= 1)
    #       SI < 2 facteur de gravité mineur => FIN6
    implies_done(And(cond1, cond2, facteurs_gravite_mineur < 2), 6)
    #       SI >= 2 facteurs de gravité mineurs => FIN4
    implies_done(And(cond1, cond2, facteurs_gravite_mineur >= 2), 4)
    #
    # SI fièvre OU (pas de fièvre et (diarrhée OU (toux ET douleurs) OU (toux ET anosmie))
    cond3 = Or(fievre, And(Not(fievre), Or(diarrhee, And(toux, douleurs), And(toux, anosmie))))
    #    SI 0 facteur pronostique
    cond4 = (facteurs_pronostiques == 0)
    #       SI 0 facteur de gravité mineur
    cond5 = (facteurs_gravite_mineur == 0)
    #          SI moins de 50 ans => FIN2
    implies_done(And(cond3, cond4, cond5, age < 50), 2)
    #          SINON => FIN3
    implies_done(And(cond3, cond4, cond5, Not(age < 50)), 3)
    #       SI >= 1 facteur de gravité mineur => FIN3
    implies_done(And(cond3, cond4, facteurs_gravite_mineur >= 1), 3)
    #    SI >= 1 facteurs pronostiques
    cond6 = (facteurs_pronostiques >= 1)
    #       SI < 2 facteur de gravité mineur => FIN3
    implies_done(And(cond3, cond6, facteurs_gravite_mineur < 2), 3)
    #       SI >= 2 facteurs de gravité mineurs => FIN4
    implies_done(And(cond3, cond6, facteurs_gravite_mineur >= 2), 4)
    #
    # SI toux OU douleurs OU anosmie
    cond7 = Or(toux, douleurs, anosmie)
    #    SI 0 facteur pronostique => FIN2
    implies_done(And(cond7, facteurs_pronostiques == 0), 2)
    #    SI >= 1 facteur pronostique => FIN7
    implies_done(And(cond7, facteurs_pronostiques >= 1), 7)
    #
    # SI NI toux NI douleurs NI anosmie => FIN8
    implies_done(And(Not(toux), Not(douleurs), Not(anosmie)), 8)

    return done

# Maintenant que l'algorithme est encodé dans Z3, on peut essayer de prouver
# les théorèmes. Cette fonction wrappe la fonction "check" de Z3 avec une
# interface qui affiche la valeur des variables du contre-exemple si le théorème
# que l'on essaye de prouver est faux.


def check_and_print(done_arrays, msg_unsat, msg_sat):
    c = s.check()
    if c == sat:
        print(msg_sat)
        print("  Valeur des variables :")
        variables_evaluated = ["    {}: {}".format(
            v, s.model().evaluate(v, model_completion=True)) for v in variables]
        print(*variables_evaluated, sep="\n")
        print("  Sorties activées :")
        for (done, msg) in done_arrays:
            done_evaluated = [("    FIN{} ({})".format(
                i + 1, msg), s.model().evaluate(d, model_completion=True)) for (i, d) in enumerate(done)]
            done_evaluated = [a for (a, b) in done_evaluated if b]
            print(*done_evaluated, sep="\n")
        print("")
    elif c == unsat:
        print(msg_unsat, "\n")


done = algo_6e5e17a()
done_msg = "https://github.com/Delegation-numerique-en-sante/covid19-algorithme-orientation-arbre-de-decision/blob/6e5e17a2d546f95327556a78c23ecd97bae619d8/covid19-orientation-arbre-de-decision.txt"


def check_all_cases_ending(done, msg):
    print(">>> Théorème: l'algorithme {} termine dans tous les cas".format(msg))
    s.push()
    # Comment encoder ce théorème dans Z3 ? On se rappelle que "done" contient
    # la liste de tous les cas de terminaisons, exprimés comme des formules logiques
    # en fonction des variables d'entrée.
    # Pour savoir si l'on termine dans tous les cas, il suffit de faire un "ou"
    # logique de toutes les conditions de terminaison. En effet, si l'on passe
    # la négation de ce "ou" à Z3, alors le solveur va essayer de trouver une
    # combinaison des variables d'entrées telle que l'on ne soit dans aucun des
    # cas de terminaison prévu. Si le solveur ne trouve pas de telle combinaison,*
    # alors le théorème est prouvé.
    cond = Or(done)
    s.add(Not(cond))
    check_and_print([(done, msg)], "OK !",
                    "Trouvé un cas de non-terminaison !")
    s.pop()


check_all_cases_ending(done, done_msg)


def check_every_exit_reached(done, msg):
    print(">>> Théorème: pour l'algorithme {} chaque sortie est atteignable".format(msg))
    for i in range(0, DONE_LENGTH):
        print("Atteindre la sortie FIN{} ?".format(i + 1))
        s.push()
        # Ici, la condition à donner à Z3 est simplement done[i] car il s'agit
        # de trouver une manière d'atteindre la sortie i.
        cond = done[i]
        s.add(cond)
        check_and_print(
            [(done, msg)], "Sortie jamais atteinte !", "Valeur d'atteinte:")
        s.pop()


check_every_exit_reached(done, done_msg)


# Cette fonction permet de comparer deux algorithmes entre eux en essayant
# de trouver des situations sur lesquelles les deux algorithmes donnent des
# sorties différentes. Si il n'existe pas de telle situation, alors les
# deux algorithmes sont fonctionnellement équivalents, i.e. ils retournent
# toujours le même résultat dans tous les cas.
def check_same(done1, msg1, done2, msg2):
    print(">>> Théorème: deux algorithmes ({} et {}) donnent les mêmes réponses".format(
        msg1, msg2))
    for i in range(0, DONE_LENGTH):
        print("Analyse de la sortie FIN{}:".format(i + 1))
        s.push()
        # La condition est : on veut que l'un des deux algorithmes affiche
        # la sortie voulue mais pas l'autre, d'où le Xor.
        cond = Xor(done1[i], done2[i])
        s.add(cond)
        check_and_print([(done1, msg1), (done2, msg2)],
                        "Les deux algorithmes sont toujours d'accord sur cette sortie !", "Trouvé une valeur de discorde:")
        s.pop()
    print("")
