(ns choices.custom)

;; This file can contain at least two defns:
;;
;; (defn preprocess-scores [scores] ...)
;; (defn conditional-score-result [scores conditional-score-outputs] ...)
;;
;; You cannot use other names than those.
;; You can add utility functions.

(defn compute-bmi [^number p ^number t]
  (.toFixed (/ p (Math/pow (/ t 100.0) 2)) 2))

(defn preprocess-scores [scores]
  (let [temperature (:value (:temperature scores))
        fever_value (or (= temperature "inf_35.5")
                        (= temperature "sup_39")
                        (= temperature "NSP"))
        fever-map   {:fever {:value fever_value :display "Fièvre"}}
        bmi-val     (compute-bmi (:value (:weight scores))
                                 (:value (:height scores)))
        bmi-map     {:bmi {:value bmi-val :display "BMI"}}
        scores      (merge (dissoc scores :fever) fever-map bmi-map)
        scores      (update-in scores [:pronostic-factors :value]
                               #(if (>= bmi-val 30) (inc %) %))]
    ;; Return preprocessed scores:
    scores))

;; Available variables:
;; age_range
;; weight
;; height
;; fever
;; cough
;; agueusia_anosmia
;; sore_throat_aches
;; diarrhea
;; minor-severity-factors
;; major-severity-factors
;; pronostic-factors
(defn conditional-score-result [resultats conclusions]
  (let [{:keys [age_range fever cough agueusia_anosmia
                sore_throat_aches diarrhea
                minor-severity-factors
                major-severity-factors
                pronostic-factors]} resultats
        ;; Set the possible conclusions
        {:keys [less_15
                domicile_surveillance_1
                consultation_surveillance_1
                consultation_surveillance_2
                SAMU
                consultation_surveillance_3
                consultation_surveillance_4
                surveillance]}      conclusions
        ;; Set the final conclusion to one of the FIN*
        conclusion
        (cond
          ;; Branche 1
          (= age_range "inf_15")
          less_15
          ;; Branche 2
          (>= major-severity-factors 1)
          SAMU
          ;; Branche 3
          (and fever cough)
          (cond (= pronostic-factors 0)
                consultation_surveillance_3
                (>= pronostic-factors 1)
                (if (< minor-severity-factors 2)
                  consultation_surveillance_3
                  consultation_surveillance_2))
          ;; Branche 4
          (or fever
              diarrhea
              (and cough sore_throat_aches)
              (and cough agueusia_anosmia)
              (and sore_throat_aches agueusia_anosmia))
          (cond (= pronostic-factors 0)
                (if (= minor-severity-factors 0)
                  (if (= age_range "from_15_to_49")
                    domicile_surveillance_1
                    consultation_surveillance_1)
                  consultation_surveillance_1)
                (>= pronostic-factors 1)
                (if (< minor-severity-factors 2)
                  consultation_surveillance_1
                  consultation_surveillance_2))
          ;; Branche 5
          (or (and cough (not sore_throat_aches) (not agueusia_anosmia))
              (and (not cough) sore_throat_aches (not agueusia_anosmia))
              (and (not cough) (not sore_throat_aches) agueusia_anosmia))
          (if (= pronostic-factors 0)
            domicile_surveillance_1
            consultation_surveillance_4)
          ;; Branche 6
          (and (not cough)
               (not sore_throat_aches)
               (not agueusia_anosmia))
          surveillance)]
    ;; Return the expected map:
    {:notification (get conclusion :notification)
     :stick-help   (get conclusion :sticky-help)
     :node         (get conclusion :node)
     :output       (get conclusion :message)}))
