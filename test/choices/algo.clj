(ns choices.algo
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [clojure.core.logic :as logic]
   [clojure.core.logic.fd :as fd]
   [clojure.test :refer [deftest is testing run-tests]]
   [choices.macros :refer [inline-yaml-resource]]))

;; General configuration
(def config (inline-yaml-resource "config.yml"))
(def conditional-score-outputs (:conditional-score-outputs config))

(s/def ::age (s/int-in 14 66))
(s/def ::weight (s/int-in 59 111)) ;; kgs
(s/def ::height (s/int-in 139 191)) ;; cm
(s/def ::cough boolean?)
(s/def ::fever boolean?)
(s/def ::agueusia_anosmia boolean?)
(s/def ::sore_throat_aches boolean?)
(s/def ::diarrhea boolean?)
(s/def ::pronostic-factors (s/int-in 0 2))
(s/def ::minor-severity-factors (s/int-in 0 2))
(s/def ::major-severity-factors (s/int-in 0 2))

(defn compute-bmi [p t] (/ p (Math/pow (/ t 100.0) 2)))

(s/def ::response (s/keys :req-un [::age
                                   ::weight
                                   ::height
                                   ::fever
                                   ::cough
                                   ::agueusia_anosmia
                                   ::sore_throat_aches
                                   ::diarrhea
                                   ::pronostic-factors
                                   ::minor-severity-factors
                                   ::major-severity-factors]))

(defn get-age-range [scores]
  (cond (< (:age scores) 15) "inf_15"
        (< (:age scores) 50) "from_15_to_49"
        (< (:age scores) 65) "from_50_to_64"
        :else                "sup_65"))

(defn preprocess-scores [scores]
  (let [bmi-val   (or (:bmi scores)
                      (compute-bmi (:weight scores)
                                   (:height scores)))
        age-range (or (not-empty (:age-range scores))
                      (get-age-range scores))
        scores    (merge scores {:bmi bmi-val})
        scores    (merge scores {:age-range age-range})
        scores    (update-in scores [:pronostic-factors]
                             #(if (= (:age-range scores) "sup_65") (inc %) %))
        scores    (update-in scores [:pronostic-factors]
                             #(if (>= bmi-val 30) (inc %) %))
        scores    (dissoc scores :weight :height :age)]
    ;; Returned preprocessed scores:
    scores))

(defn conditional-score-result [response & [println?]]
  (let [;; Set the possible final orientations:
        {:keys [less_15
                consultation_surveillance_1
                consultation_surveillance_2
                SAMU
                consultation_surveillance_3
                consultation_surveillance_4
                surveillance]}      conditional-score-outputs
        ;; Preprocess the response to set age-range, bmi, and possibly
        ;; increment pronostic-factors and minor/major-severity-factors:
        response                    (preprocess-scores response)
        ;; Get the value needed for computing the orientation:
        {:keys [age-range fever cough agueusia_anosmia
                sore_throat_aches diarrhea
                minor-severity-factors
                major-severity-factors
                pronostic-factors]} response
        ;; Set the final conclusion to one of the orientation message:
        conclusion
        (cond
          ;; Branche 1
          (= age-range "inf_15")
          (do (when println? (println "Branch 1: less than 15 years"))
              less_15)
          ;; Branche 2
          (>= major-severity-factors 1)
          (do (when println? (println "Branch 2: at least one major gravity factor"))
              SAMU)
          ;; Branche 3
          (and fever cough)
          (do (when println? (println "Branch 3: fever and cough"))
              (cond (= pronostic-factors 0)
                    consultation_surveillance_3
                    (>= pronostic-factors 1)
                    (if (< minor-severity-factors 2)
                      consultation_surveillance_3
                      consultation_surveillance_2)))
          ;; Branche 4
          (or fever
              diarrhea
              (and cough sore_throat_aches)
              (and cough agueusia_anosmia)
              (and sore_throat_aches agueusia_anosmia))
          (do (when println? (println "Branch 4: fever or other symptoms"))
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
          (do (when println? (println "Branch 5: no fever and only one other symptom"))
              (if (= pronostic-factors 0)
                consultation_surveillance_3
                consultation_surveillance_4))
          ;; Branche 6
          (and (not cough)
               (not sore_throat_aches)
               (not agueusia_anosmia))
          (do (when println? (println "Branche 6: no symptom at all"))
              surveillance))]
    ;; Return the expected map:
    {:res response
     :msg (get conclusion :message)}))

(def all-inputs
  (logic/run* [q]
    (logic/fresh [fever cough agueusia_anosmia
                  sore_throat_aches diarrhea
                  pronostic-factors
                  minor-severity-factors
                  major-severity-factors
                  age-range bmi
                  response]
      (logic/membero
       age-range ["inf_15" "from_15_to_49" "from_50_to_64" "sup_65"])
      (fd/in bmi (fd/interval 29 30))
      (logic/membero fever [true false])
      (logic/membero cough [true false])
      (logic/membero agueusia_anosmia [true false])
      (logic/membero sore_throat_aches [true false])
      (logic/membero diarrhea [true false])
      (fd/in pronostic-factors (fd/interval 0 12))
      (fd/in minor-severity-factors (fd/interval 0 2))
      (fd/in major-severity-factors (fd/interval 0 2))
      (logic/== response {:fever                  fever
                          :cough                  cough
                          :agueusia_anosmia       agueusia_anosmia
                          :age-range              age-range
                          :sore_throat_aches      sore_throat_aches
                          :diarrhea               diarrhea
                          :bmi                    bmi
                          :pronostic-factors      pronostic-factors
                          :minor-severity-factors minor-severity-factors
                          :major-severity-factors major-severity-factors})
      (logic/== q response))))

(def all-results (map conditional-score-result all-inputs))
(def all-results-no-nil (remove nil? all-results))

(deftest each-input-as-a-conclusion?
  (testing "Is each input reaching a conclusion?"
    (is (= (count all-results) (count all-results-no-nil)))))

(deftest all-conclusions-are-reached?
  (testing "Is each conclusion reached at least once?"
    (is (= (count (distinct (map :msg all-results))) 7))))

(defn -main [& [arg]]
  (let [samples (gen/sample
                 (s/gen ::response)
                 (or (and (not= "logic" arg) (not-empty arg) (Integer. arg)) 1))]
    (if (= arg "logic")
      (run-tests 'choices.algo)
      (doseq [sample samples]
        (let [{:keys [res msg]} (conditional-score-result sample true)]
          (println "Réponses: " res)
          (println "Conclusion: " msg))))))
