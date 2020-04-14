;; Copyright (c) 2019-2020 DINSIC, Bastien Guerry <bastien.guerry@data.gouv.fr>
;; SPDX-License-Identifier: EPL-2.0
;; License-Filename: LICENSES/EPL-2.0.txt

(ns choices.corebis
  (:require-macros [choices.macros :refer [inline-yaml-resource]])
  (:require [reagent.core :as reagent]
            [reagent.dom]
            [reagent.format :as fmt]
            [reagent.session :as session]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [choices.i18n :as i18n]
            [choices.custom :as custom]
            [cljsjs.clipboard]
            [cljs.reader]
            [clojure.string :as string]
            [markdown-to-hiccup.core :as md]
            [taoensso.tempura :refer [tr]]))

;; General configuration
(def config (inline-yaml-resource "config-bis.yml"))

;; Variables
(def show-help-global (reagent/atom (:display-help config)))
(def show-help (reagent/atom (:display-help config)))
(def show-modal (reagent/atom false))
(def show-summary-answers (reagent/atom true))
(def modal-message (reagent/atom ""))
(def show-summary (:display-summary config))
(def conditional-score-outputs (:conditional-score-outputs config))
(def sticky-help (reagent/atom ""))

;; home-page and start-page
(def home-page
  (first (remove nil? (map #(when (:home-page %) (keyword (:node %)))
                           (:tree config)))))
(def start-page
  (first (remove nil? (map #(when (:start-page %) (keyword (:node %)))
                           (:tree config)))))

(defn md-to-string [^string s]
  (-> s (md/md->hiccup) (md/component)))

;; History-handling variables
(def history (reagent/atom [{:score (:score-variables config)}]))
(def hist-to-redo (reagent/atom {}))
(def hist-to-add (reagent/atom {}))

;; Localization variables
(def localization-custom
  (into {} (map (fn [locale] {(key locale)
                              (merge (val locale) (:ui-strings config))})
                i18n/localization)))
(def lang (keyword (or (not-empty (:locale config)) "en")))
(def opts {:dict localization-custom})
(def i18n (partial tr opts [lang]))

;; Create a copy-to-clipboard component
(defn clipboard-button [label target]
  (let [clipboard-atom (reagent/atom nil)]
    (reagent/create-class
     {:display-name "clipboard-button"
      :component-did-mount
      #(let [clipboard (new js/ClipboardJS (reagent.dom/dom-node %))]
         (reset! clipboard-atom clipboard))
      :component-will-unmount
      #(when-not (nil? @clipboard-atom)
         (reset! clipboard-atom nil))
      :reagent-render
      (fn []
        [:button.button.is-info.is-light.is-size-4
         {:title                 (i18n [:copy-to-clipboard])
          :data-clipboard-target target}
         label])})))

;; Utility functions
(defn strip-html-tags [^string s]
  (if (string? s) (string/replace s #"<([^>]+)>" "") s))

(defn sort-map-by-score-values [m]
  (into (sorted-map-by
         (fn [k1 k2] (compare [(:value (get m k2)) k2]
                              [(:value (get m k1)) k1])))
        m))

(defn all-vals-compare? [f m1 m2]
  (every? true? (for [[k v] m1] (f v (k m2)))))

;; Create routes
(def routes
  (into [] (for [n (:tree config)] [(:node n) (keyword (:node n))])))

;; Define multimethod for later use in `create-page-contents`
(defmulti page-contents identity)

(defn header []
  [:section {:class (str "hero " (:color (:header config)))}
   [:div.hero-body
    [:div.container
     [:div.columns
      (let [logo (:logo (:header config))]
        (when (not-empty logo)
          [:div.column
           [:figure.media-left
            [:p.image.is-256x256
             [:a {:href (rfe/href home-page)}
              [:img {:src logo}]]]]])
        [:div.column
         {:class (if (not-empty logo)
                   "has-text-right"
                   "has-text-centered")}
         [:h1.title (:title (:header config))]
         [:br]
         [:h2.subtitle
          (md-to-string (:subtitle (:header config)))]])]]]]) 

(defn modal [show-modal]
  [:div {:class (str "modal " (when @show-modal "is-active"))}
   [:div.modal-background]
   [:div.modal-content
    [:div.box
     [:div.title (i18n [:attention])]
     @modal-message
     [:br]
     [:div.has-text-centered
      [:a.button.is-medium.is-warning
       {:on-click #(reset! show-modal false)}
       (i18n [:ok])]]]]
   [:button.modal-close.is-large
    {:aria-label "close"
     :on-click   #(reset! show-modal false)}]])

(defn question-help-clipboard [done question force-help help]
  [:div.level
   [:div.is-size-3
    [:div.columns.is-multiline
     [:div.column
      (md-to-string question)]]]
   (if-not done
     ;; Not done: display the help button
     (when (and (not force-help) @show-help-global
                (not-empty help))
       [:div.level-right
        [:button.level-item.button.is-info.is-light.is-size-4
         {:title    (i18n [:display-help])
          :on-click #(swap! show-help not)}
         "💬"]])
     ;; Done: display the copy-to-clipboard button
     [:div.level-right
      [:div.level-item
       [:button.button.is-info.is-light.is-size-4
        {:title    (i18n [:toggle-summary-style])
         :on-click #(swap! show-summary-answers not)} "🔗"]]
      [:div.level-item
       [clipboard-button "📋" "#copy-this"]]])])

(defn footer []
  [:section.footer
   [:div.content.has-text-centered
    (md-to-string (:text (:footer config)))
    (when-let [c (not-empty (:contact (:footer config)))]
      [:p (i18n [:contact-intro])
       [:a {:href (str "mailto:" c)} c]])]])

(defn score-details [scores]
  (for [row-score (partition-all 5 scores)]
    ^{:key (random-uuid)}
    [:div.tile.is-ancestor
     (for [s row-score]
       ^{:key (random-uuid)}
       [:div.tile.is-parent
        (let [v     (val s)
              value (:value v)]
          [:div.tile.is-child.box
           (str (:display v) ": " value)])])]))

(defn score-top-result [scores]
  (let [final-scores  (sort-map-by-score-values scores)
        last-score    (first final-scores)
        butlast-score (second final-scores)]
    (when (> (:value (val last-score)) (:value (val butlast-score)))
      (when-let [s (:as-top-result-display (val last-score))]
        [:div.tile.is-parent.is-6
         [:p.tile.is-child.box.is-warning.notification
          (:as-top-result-display s)]]))))

(defn conditional-score-result [scores conclusions]
  (let [conditions   (atom nil)
        matching     (atom nil)
        output       (atom "")
        notification (atom "")
        node         (atom "")]
    (doseq [[_ cas] conclusions
            :let    [not (:notification cas)
                     msg (:message cas)
                     nod (:node cas)
                     pri (:priority cas)
                     cds (dissoc cas :message :notification :priority)]]
      (doseq [condition cds]
        (swap! conditions conj
               (merge (val condition) {:msg msg :not not :pri pri :nod nod}))))
    (doseq [c0   @conditions
            :let [c  (dissoc c0 :msg :not :pri :nod)
                  ks (keys c)]]
      (when (all-vals-compare?
             (fn [a b] (if (zero? a) (= a b) (>= a b)))
             (select-keys scores ks) c)
        (swap! matching conj c0)))
    (let [match (first (sort-by :pri @matching))]
      (reset! output (:msg match))
      (reset! notification (:not match))
      (reset! node (:nod match)))
    ;; Return the expected map:
    {:notification @notification
     :output       @output
     :node         @node}))

(defn scores-result [scores]
  (let [scores (if (resolve 'custom/preprocess-scores)
                 (custom/preprocess-scores scores)
                 scores)]
    [:div
     (when (:display-score config)
       [:div.is-6
        ;; Optional, mainly for debugging purpose
        (when (:display-score-details config)
          (score-details scores))
        ;; Only when no score-results
        (when (and (not conditional-score-outputs)
                   (:display-score-top-result config))
          (score-top-result scores))
        ;; Only when score-results is defined
        (let [scores (apply merge (map (fn [[k v]] {k (:value v)}) scores))]
          (when conditional-score-outputs
            (let [{:keys [notification output]}
                  (if (resolve 'custom/conditional-score-result)
                    (custom/conditional-score-result
                     scores conditional-score-outputs)
                    (conditional-score-result
                     scores conditional-score-outputs))]
              (when (not-empty output)
                [:div.tile.is-parent
                 [:div.tile.is-size-4.is-child
                  {:class (str (or (not-empty notification) "is-info")
                               " notification subtitle")}
                  (md-to-string output)]]))))
        ;; Always display display-unconditionally when not empty
        (when-let [sticky (:display-unconditionally config)]
          [:div.tile.is-parent
           [:div.tile.is-size-4.is-child.notification.subtitle
            (md-to-string sticky)]])])
     [:br]]))

(defn summary []
  (for [o (if @show-summary-answers
            (remove nil? (reverse (:answers (peek @history))))
            (remove nil? (reverse (:questions (peek @history)))))]
    ^{:key (random-uuid)}
    (cond
      (and (string? o) (not-empty o))
      [:div.tile.is-parent.is-horizontal.notification
       {:key (random-uuid)}
       [:div.title.is-child
        [:div.subtitle (md-to-string o)]]]
      (not-empty (butlast o))
      [:div.tile.is-parent.is-horizontal.notification
       {:key (random-uuid)}
       (for [n (butlast o)]
         (when (not-empty n)
           [:div.tile.is-child.subtitle {:key (random-uuid)} (md-to-string n)]))
       (when-let [a (not-empty (peek o))]
         [:div.tile.is-child.subtitle.has-text-centered.has-text-weight-bold.is-size-4
          {:key (random-uuid)}
          (md-to-string a)])])))

(defn restart-mailto-buttons []
  [:div.level-right
   [:a.button.level-item.is-info.is-light.is-size-4
    {:title (i18n [:redo])
     :href  (rfe/href start-page)} "🔃"]
   (when (not-empty (:mail-to config))
     (let [contents (or (not-empty (remove nil? (:answers (peek @history))))
                        (not-empty (remove nil? (:questions (peek @history))))
                        (i18n [:mail-body-default]))
           body     (->> contents flatten
                         (map strip-html-tags)
                         (string/join "%0D%0A%0D%0A")
                         (fmt/format (i18n [:mail-body])))]
       [:a.button.level-item.is-info.is-light.is-size-4
        {:title (i18n [:mail-to-message])
         :href  (str "mailto:" (:mail-to config)
                     "?subject=" (i18n [:mail-subject])
                     "&body="
                     (string/replace body #"[\n\t]" "%0D%0A%0D%0A"))}
        "📩"]))])

(defn get-target-node [goto current-score]
  (cond (string? goto)
        (keyword goto)
        (map? goto)
        (if (and (:conditional-navigation config)
                 (:conditional-score-outputs goto))
          (let [score  (if (resolve 'custom/preprocess-scores)
                         (custom/preprocess-scores current-score)
                         current-score)
                score  (apply merge (map (fn [[k v]] {k (:value v)}) score))
                result (if (resolve 'custom/conditional-score-result)
                         (custom/conditional-score-result
                          score conditional-score-outputs)
                         (conditional-score-result
                          score conditional-score-outputs))]
            (when-let [sticky-help-msg (:stick-help result)]
              (reset! sticky-help sticky-help-msg))
            (keyword (or (:node result) (get goto :default))))
          (let [score   (apply merge (map (fn [[k v]] {k (:value v)}) current-score))
                matches (doall
                         (for [[cnd-name value-node]
                               goto
                               :let [kname (keyword cnd-name)
                                     cnd-val  (:value value-node)
                                     cnd-node (:node value-node)]]
                           (when (and (= cnd-val (get score kname))
                                      (string? cnd-node))
                             cnd-node)))]
            (keyword (or (first (remove nil? matches))
                         (get goto :default)))))))

(defn merge-scores [previous_score current_score]
  (merge-with
   (fn [a b] {:display               (:display a)
              :as-top-result-display (:as-top-result-display a)
              :value
              (let [a_v (:value a) b_v (:value b)]
                (cond (and (integer? a_v) (integer? b_v))
                      (+ (:value a) (:value b))
                      ;; one boolean and/or one strings, take last:
                      :else b_v))})
   previous_score current_score))

(defn display-choices [choices text no-summary]
  (for [choices-row (partition-all 4 choices)]
    ^{:key (random-uuid)}
    [:div.tile.is-parent.is-horizontal
     (doall
      (for [{:keys [answer goto explain color summary score]} choices-row]
        ^{:key (random-uuid)}
        [:div.tile.is-child
         {:class (if (> (count choices) 3) "is-3" "")}
         [:a.tile
          {:style {:text-decoration "none"}
           :on-click
           #(do (when (vector? summary)
                  (reset! show-modal true)
                  (reset! modal-message (md-to-string (peek summary))))
                (let [current-score
                      (merge-scores (:score (peek @history)) score)]
                  (reset! hist-to-add
                          (merge
                           {:score current-score}
                           {:questions (when-not no-summary [text answer])}
                           {:answers summary}))
                  (rfe/push-state
                   (get-target-node goto current-score))))}
          [:div.card-content.tile.is-parent.is-vertical
           [:div.tile.is-child.box.is-size-4.notification.has-text-centered
            {:class (or (not-empty color) "is-info")}
            (md-to-string answer)]
           (when (and explain @show-help)
             [:div.tile.is-child.subtitle
              (md-to-string explain)])]]]))]))

;; Create all the pages
(defn create-page-contents [{:keys [done node text help no-summary
                                    progress force-help choices]}]
  (defmethod page-contents (keyword node) []
    [:div
     (when (not-empty (:header config))
       (header))
     [:div.container
      (modal show-modal)
      [:div.section
       (if-let [[v m] (cljs.reader/read-string progress)]
         [:div [:progress.progress.is-success
                {:value v :max m}]
          [:br]])
       (when-let [sticky-help-message (not-empty @sticky-help)]
         [:div.notification.is-size-5
          {:class (or (:sticky-help-color config)
                      "is-warning")}
          (md-to-string sticky-help-message)])
       (question-help-clipboard done text force-help help)
       (when (or force-help @show-help)
         (when-let [help-message (not-empty help)]
           [:div.notification.is-size-5
            (md-to-string help-message)]))
       (if-not done
         ;; Not done: display the choices
         [:div.tile.is-ancestor.is-vertical
          (display-choices choices text no-summary)]
         ;; Done: display the final summary-answers
         [:div
          [:div.tile.is-ancestor {:id "copy-this"}
           [:div.tile.is-parent.is-vertical.is-12
            ;; Display score
            (if-let [scores (:score (peek @history))]
              (scores-result scores)
              [:br])
            ;; Display answers
            (when show-summary (summary))]]
          (restart-mailto-buttons)])]]
     (when (not-empty (:footer config))
       (footer))]))

;; Create all the pages from config.yml
(dorun (map create-page-contents (:tree config)))

;; Create component to mount the current page
(defn current-page []
  (let [page (or (session/get :current-page) home-page)]
    [:div ^{:key random-uuid} [page-contents page]]))

;; Setup navigation
(defn on-navigate [match]
  (let [target-page (:name (:data match))
        prev        (peek @history)]
    (cond
      ;; Reset history?
      (= target-page start-page)
      (do (reset! history [{:score (:score-variables config)}])
          (reset! hist-to-redo {})
          (reset! hist-to-add {}))
      ;; History backward?
      (= target-page (first (:page (peek @history))))
      (reset! history (into [] (butlast @history)))
      ;; History forward?
      (= target-page (first (:page @hist-to-redo)))
      (swap! history conj @hist-to-redo)
      :else
      (swap! history
             conj {:page      (conj (:page prev) (session/get :current-page))
                   :questions (conj (:questions prev) (:questions @hist-to-add))
                   :answers   (conj (:answers prev) (:answers @hist-to-add))
                   :score     (conj (:score prev) (:score @hist-to-add))}))
    (reset! hist-to-redo (peek @history))
    (session/put! :current-page target-page)))

;; Initialize the app
(defn ^:export init []
  (rfe/start!
   (rf/router routes)
   on-navigate
   {:use-fragment true})
  (reagent.dom/render
   [current-page]
   (. js/document (getElementById "app"))))
