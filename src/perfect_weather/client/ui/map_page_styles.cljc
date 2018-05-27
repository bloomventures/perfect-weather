(ns perfect-weather.client.ui.map-page-styles
  (:require
    [perfect-weather.client.ui.footer-styles :refer [>footer]]
    [perfect-weather.client.ui.colors :as colors]
    [perfect-weather.client.ui.mixins :as mixins]))

(defn >map-page []
  [:>.page.map
   {:display "flex"
    :min-height "100vh"
    :flex-direction "column"
    :justify-content "center"
    :align-items "center"
    :width "100%"}

   [:>.controls
    {:width "100%"
     :max-width "800px"}

    [:>.months
     {:display "flex"
      :justify-content "space-between"
      :width "100%"
      :-webkit-user-select "none"
      :-moz-user-select "none"
      :-ms-user-select "none"
      :user-select "none"}
     (mixins/tiny-text)

     [:>.month
      {:text-align "center"
       :width (str (float (/ 100 12)) "%")}
      (mixins/alternating-colors)]]]

   [:>.map
    {:margin "0 auto"}]

   [:>.gap
    {:flex-grow 1}]

   (>footer)])
