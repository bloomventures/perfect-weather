(ns perfect-weather.client.styles)

(defn styles []
  [
   [:body :h1 :h2 :p
    {:margin 0
     :padding 0}]

   [:#app
    
    [:>.app
     {:display "flex"
      :min-height "100vh"
      :flex-direction "column"
      :justify-content "center"
      :align-items "center"}

     [:>form
      {:font-size "2rem"}

      [:>input
        {:font-size "2rem"
         :width "8em"
         :padding "0.25rem"}]]
     
     [:>.results
      
      [:>.result
       {:display "flex"
        :margin "2em 0"}]]]]])
