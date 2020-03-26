(ns choices.vars
  (:require [clojure.walk :as walk]
            [choices.views :as views]
            [hiccup.page :as h]
            [clojure.walk :as walk]
            [markdown-to-hiccup.core :as md]
            [choices.macros :refer [inline-yaml-resource]]))

(def bulma-class-replacements
  {:h1 :h1.title
   :ul :ul.list
   :li :li.list-item})

(defn md-to-string [s]
  (-> s (md/md->hiccup) (md/component)))

(defn fix-ordered-map [om]
  (walk/prewalk (fn [n] (if (map? n) (into {} n) n)) om))

(def config (inline-yaml-resource "config.yml"))

(def conditional-score-outputs (fix-ordered-map (:conditional-score-outputs config)))

(def index-fr-meta (inline-yaml-resource "website/fr/index-meta.yml"))

(def index-fr-contents (walk/prewalk-replace
                        bulma-class-replacements
                        (md-to-string (slurp "website/fr/index-contents.md"))))

(def repl-fr-meta (inline-yaml-resource "website/fr/repl-meta.yml"))

(def exemple-de-reponse {
                         :fievre                   1,
                         :diarrhees                0,
                         :toux                     1,
                         :mal-de-gorge             0,
                         :anosmie                  0,
                         :age                      62,
                         :poids                    63,
                         :taille                   167,
                         :facteurs-pronostique     2,
                         :facteurs-gravite-mineurs 1,
                         :facteurs-gravite-majeurs 0})

(def resultat "(defn resultat [reponse]
  (let [{:keys [fievre diarrhees toux mal-de-gorge anosmie
                age poids taille
                facteurs-gravite-mineurs
                facteurs-gravite-majeurs
                facteurs-pronostique]} reponse
        ;; Calcule IMC et facteur âge
        reponse
        (merge reponse
               {:imc             (compute-imc poids taille)
                :moins-de-15-ans (< age 15)
                :plus-de-50-ans  (> age 50)})]
    (cond
      ;; Branche 1
      (= moins-de-15-ans 1)
      (println \"   Branche: 1 (moins de 15 ans)\")
      ;; Branche 2
      (or (and (> fievre 0) (= toux 0))
          (and (> toux 0) (> mal-de-gorge 0))
          (and (> toux 0) (> anosmie 0))
          (and (> fievre 0) (> diarrhees 0)))
      (do (println \"   Branche: 2 (fièvre ou autres symptômes)\")
          (cond (>= facteurs-gravite-majeurs 1)
                (println \"FIN5\")
                (= facteurs-pronostique 0)
                (if (= facteurs-gravite-mineurs 0)
                  (if (= plus-de-50-ans 0)
                    (println \"FIN2\")
                    (println \"FIN3\"))
                  (println \"FIN3\"))
                (>= facteurs-pronostique 1)
                (cond (< facteurs-gravite-mineurs 2)
                      (println \"FIN3\")
                      (>= facteurs-gravite-mineurs 2)
                      (println \"FIN4\"))))
      ;; Branche 3
      (>= facteurs-gravite-majeurs 1)
      (do (println \"   Branche: 3 (Un facteur majeur de gravité)\")
          (println \"FIN5\"))
      ;; Branche 4
      (and (> fievre 0) (> toux 0))
      (do (println \"   Branche: 4 (Fièvre et toux)\")
          (if (= facteurs-pronostique 0)
            (when (> facteurs-gravite-mineurs 0)
              (println \"FIN6\"))
            (when (>= facteurs-pronostique 1)
              (cond (>= facteurs-gravite-mineurs 0)
                    (println \"FIN6\")
                    (>= facteurs-gravite-mineurs 2)
                    (println \"FIN4\")))))
      ;; Branche 5
      (and (= fievre 0)
           (or (> toux 0) (> mal-de-gorge 0) (> anosmie 0)))
      (do (println \"   Branche: 5 (Pas de fièvre et un autre symptôme\")
          (cond (= facteurs-gravite-mineurs 0)
                (println \"FIN7\")
                (or (>= facteurs-gravite-mineurs 1) (>= facteurs-pronostique 1))
                (println \"FIN8\")))
      ;; Branche 6
      (and (= fievre 0) (= toux 0) (= mal-de-gorge 0) (= anosmie 0))
      (do (println \"   Branche: 6 (Pas de symptôme)\")
          (println \"FIN9\")))))")

(def repl-fr-contents
  [:section
   [:div.container
    [:br]
    [:h1.title "Code de l'indice de masse corporelle"]
    [:pre.language-klipse
     "(defn compute-imc [p t] (/ p (Math/pow (/ t 100.0) 2)))"]]

   [:div.container
    [:br]
    [:h1.title "Fonction de calcul du résultat"]
    [:pre.language-klipse resultat]]

   [:div.container
    [:br]
    [:h1.title "Variable avec un exemple de réponse"]
    [:pre.language-klipse
     (str "(def exemple-de-reponse " (pr-str exemple-de-reponse) ")")]]

   [:div.container
    [:br]
    [:h1.title "Essayez vous-même"]
    [:p.subtitle "Changez l'exemple de réponse ci-dessus et voyez le résultat."]
    [:pre.language-klipse "(resultat exemple-de-reponse)"]]])