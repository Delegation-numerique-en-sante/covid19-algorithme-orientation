(ns choices.repl.clojure.index
  (:require [clojure.tools.reader]))

(def orientations
  (slurp "src/clj/choices/repl/clojure/orientations.clj"))

(def pretraitement
  (slurp "src/clj/choices/repl/clojure/preprocess.clj"))

(def resultat
  (slurp "src/clj/choices/repl/clojure/result.clj"))

(def exemple-de-reponse
  (slurp "src/clj/choices/repl/clojure/response.clj"))

(def repl-fr-contents
  [:section

   [:div.container
    [:br]
    [:h1.title "Définition des orientations possibles"]
    [:pre.language-klipse orientations]]

   [:div.container
    [:br]
    [:h1.title "Fonctions de prétraitement"]
    [:pre.language-klipse pretraitement]]

   [:div.container
    [:br]
    [:h1.title "Fonction de calcul du résultat"]
    [:pre.language-klipse resultat]]

   [:div.container
    [:br]
    [:h1.title "Exemple de réponse"]
    [:pre.language-klipse exemple-de-reponse]]

   [:div.container
    [:br]
    [:h1.title "Orientation calculée à partir de cette réponse"]
    [:p.subtitle "Changez l'exemple de réponse ci-dessus et voyez le résultat."]
    [:pre.language-klipse "(orientation exemple-de-reponse)"]]])
