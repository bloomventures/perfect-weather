(ns perfect-weather.client.ui.map-page-styles
  (:require
    [perfect-weather.client.ui.footer-styles :refer [>footer]]))


(defn >map-page []
  [:>.page.map

   [:>.controls

    [:>.months
     {:display "flex"
      :justify-content "space-between"
      :width "100%"}

     [:>.month
      {:border-left "1px solid #ccc"
       :text-align "center"
       :width (str (float (/ 100 12)) "%")}

      [:&:last-child
       {:border-right "1px solid #ccc"}]]]]

   (>footer)])
