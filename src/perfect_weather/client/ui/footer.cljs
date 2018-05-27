(ns perfect-weather.client.ui.footer
  (:require
    [perfect-weather.client.state.routes :as routes]))

(defn footer-view []
  [:div.footer

   [:div
    [:a {:href (routes/index-path)}
     "Search"]]

   [:div
    [:a {:href (routes/faq-path)}
     "FAQ"]]

   [:div
    [:a {:href (routes/map-path)}
     "Map"]]

   [:div
    "Built by " 
    [:a {:href "https://bloomventures.io/"
         :rel "noopener"
         :target "_blank"}
     "Bloom"]]

   [:div
    "Powered by "
    [:a {:href "https://darksky.net/poweredby/"
         :rel "noopener"
         :target "_blank"}
     "DarkSky"] " & "
    [:a {:href "https://developers.google.com/places/"}
     "Google"]]])
