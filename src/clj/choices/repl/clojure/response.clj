;; Vous pouvez modifier les valeurs ci-dessous.
(def exemple-de-reponse
  {:fever                  true  ; Dépend de la température
   :diarrhea               false ; Idem
   :cough                  true  ; Idem
   :sore_throat_aches      false ; Idem
   :agueusia_anosmia       false ; Idem
   :age                    62    ; Valeur normale
   :weight                 63    ; En kgs
   :height                 167   ; En centimètres
   :pronostic-factors      0     ; Voir première série de questions
   :minor-severity-factors 0     ; Fièvre >= 39°C ou alitement >= 50% temps diurne
   :major-severity-factors 0     ; Gêne respiratoire ou difficulté manger/boire
   })
