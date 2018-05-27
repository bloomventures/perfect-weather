(ns perfect-weather.client.ui.map-page-styles
  (:require
    [perfect-weather.client.ui.footer-styles :refer [>footer]]))


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

     [:>.month
      {:border-left "1px solid #ccc"
       :text-align "center"
       :width (str (float (/ 100 12)) "%")}

      [:&:last-child
       {:border-right "1px solid #ccc"}]]]]

   [:>.map
    {:margin "0 auto"}]

   [:>.gap
    {:flex-grow 1}]

   (>footer)])
