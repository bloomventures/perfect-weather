(ns perfect-weather.client.ui.mixins
  (:require
    [garden.stylesheet :refer [at-media]]
    [perfect-weather.client.ui.colors :as colors]))

(def mobile {:max-width "760px"})

(defn tiny-text []
  {:text-transform "uppercase"
   :font-size "0.65rem"
   :line-height "1rem"
   :letter-spacing "0.03rem"})

(defn small-text []
  {:font-size "0.85rem"
   :text-transform "uppercase"
   :letter-spacing "0.1em"})

(defn alternating-colors []
  [:&
   {:border-left [["1px" "solid" colors/grid-border]]}

   ["&:nth-child(even)"
    {:background colors/grid-background}]

   [:&:last-child
    {:border-right [["1px" "solid" colors/grid-border]]}]

   (at-media mobile
     [:&:first-child
      {:border-left "none"}]

     [:&:last-child
      {:border-right "none"}])])
