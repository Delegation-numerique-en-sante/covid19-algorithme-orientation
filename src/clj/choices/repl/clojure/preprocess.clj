;; Fonction de calcul de l'indice de masse corporelle
(defn compute-bmi [weight height]
  (/ weight (Math/pow (/ height 100.0) 2)))

;; Fonction pour réduire l'âge à la tranche d'âge
(defn get-age-range [reponse]
  (cond (< (:age reponse) 15) "inf_15"
        (< (:age reponse) 50) "from_15_to_49"
        (< (:age reponse) 70) "from_50_to_64"
        :else                 "sup_65"))

;; Fonction pour mettre le bmi, age-range dans la réponse, et
;; incrémenter les facteurs pronostiques ou de gravité
(defn preprocess-scores [reponse]
  (let [bmi-val   (compute-bmi (:weight reponse)
                               (:height reponse))
        age-range (get-age-range reponse)
        reponse   (merge reponse {:bmi bmi-val})
        reponse   (merge reponse {:age-range age-range})
        reponse   (update-in reponse [:pronostic-factors]
                             #(if (= (:age-range reponse) "sup_65") (inc %) %))
        reponse   (update-in reponse [:pronostic-factors]
                             #(if (>= bmi-val 30) (inc %) %))
        reponse   (dissoc reponse :weight :height :age)]
    ;; Renvoie la réponse remise en forme:
    reponse))
