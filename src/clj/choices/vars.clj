(ns choices.vars
  (:require [clojure.walk :as walk]
            [markdown-to-hiccup.core :as md]
            [choices.macros :refer [inline-yaml-resource]]))

(def bulma-class-replacements
  {:h1  :h1.title
   :h3  :h3.is-size-4
   :h4  :h4.is-size-5
   :ul  :ul.list
   :ol  :ol.list
   :li  :li.list-item.is-size-6
   :p   :p.is-size-6
   :pre :pre.is-size-6
   })

(defn md-to-string [s]
  (-> s (md/md->hiccup) (md/component)))

(defn fix-ordered-map [om]
  (walk/prewalk (fn [n] (if (map? n) (into {} n) n)) om))

(def config (inline-yaml-resource "config.yml"))

(def conditional-score-outputs
  (fix-ordered-map (:conditional-score-outputs config)))

(def index-fr-meta
  (inline-yaml-resource "website/index-meta.yml"))

(def algo-fr-meta
  (inline-yaml-resource "website/algo-meta.yml"))

(def index-fr-contents
  (walk/prewalk-replace
   bulma-class-replacements
   (md-to-string (slurp "website/index-contents.md"))))

(def algo-fr-contents
  (walk/prewalk-replace
   bulma-class-replacements
   (md-to-string (slurp "website/algorithme-orientation-covid19.md"))))

(def repl-fr-meta
  (inline-yaml-resource "website/repl-meta.yml"))
