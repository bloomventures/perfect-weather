(ns perfect-weather.client.ui.footer-styles
  (:require
    [perfect-weather.client.ui.colors :as colors]))

(defn >footer []
  [:>.footer
   {:display "flex"
    :justify-content "center"
    :flex-wrap "wrap"
    :box-sizing "border-box"
    :width "100%"
    :padding "0.25rem"}

   [:>div
    {:color colors/text-light
     :margin "0.25rem 0.5rem"
     :white-space "nowrap"}

    [:>a
     {:color colors/accent
      :text-decoration "none"}]]])
