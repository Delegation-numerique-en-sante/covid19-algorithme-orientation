;; Fonction pour calculer l'orientation en fonction de la réponse
(defn orientation [reponse]
  ;; Initialisation des valeurs d'entrées
  (let [reponse
        (preprocess-scores reponse)
        {:keys [bmi age-range agueusia_anosmia
                fever diarrhea cough sore_throat_aches
                minor-severity-factors
                major-severity-factors
                pronostic-factors]} reponse
        {:keys
         [less_15
          consultation_surveillance_1
          consultation_surveillance_2
          SAMU
          consultation_surveillance_3
          consultation_surveillance_4
          surveillance]}            orientations]
    ;; L'algorithme COVID19 proprement dit
    (cond
      ;; Branche 1
      (= age-range "inf_15")
      (do (println "Branch 1: less than 15 years")
          less_15)
      ;; Branche 2
      (>= major-severity-factors 1)
      (do (println "Branch 2: at least one major gravity factor")
          SAMU)
      ;; Branche 3
      (and fever cough)
      (do (println "Branch 3: fever and cough")
          (cond (= pronostic-factors 0)
                consultation_surveillance_3
                (>= pronostic-factors 1)
                (if (< minor-severity-factors 2)
                  consultation_surveillance_4
                  consultation_surveillance_2)))
      ;; Branche 4
      (or fever
          diarrhea
          (and cough sore_throat_aches)
          (and cough agueusia_anosmia)
          (and sore_throat_aches agueusia_anosmia))
      (do (println "Branch 4: fever and other symptoms")
          (cond (= pronostic-factors 0)
                (if (= minor-severity-factors 0)
                  (if (= age-range "from_15_to_49")
                    consultation_surveillance_3
                    consultation_surveillance_1)
                  consultation_surveillance_1)
                (>= pronostic-factors 1)
                (if (< minor-severity-factors 2)
                  consultation_surveillance_1
                  consultation_surveillance_2)))
      ;; Branche 5
      (or (and cough (not sore_throat_aches) (not agueusia_anosmia))
          (and (not cough) sore_throat_aches (not agueusia_anosmia))
          (and (not cough) (not sore_throat_aches) agueusia_anosmia))
      (do (println "Branch 5: no fever and one other symptom")
          (if (= pronostic-factors 0)
            consultation_surveillance_3
            consultation_surveillance_4))
      ;; Branche 6
      (and (not cough)
           (not sore_throat_aches)
           (not agueusia_anosmia))
      (do (println "Branche 6: no symptom")
          surveillance))))
