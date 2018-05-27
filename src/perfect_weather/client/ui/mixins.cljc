(ns perfect-weather.client.ui.mixins)

(defn tiny-text []
  {:text-transform "uppercase"
   :font-size "0.65rem"
   :line-height "1rem"
   :letter-spacing "0.03rem"})

(defn small-text []
  {:font-size "0.85rem"
   :text-transform "uppercase"
   :letter-spacing "0.1em"})
