(ns perfect-weather.client.ui.faq-page-styles
  (:require
    [perfect-weather.client.ui.colors :as colors]))

(defn >faq-page []
  [:>.faq-page
   {:line-height "1.35"
    :margin "0 2rem"}

   [:>.header
    {:margin-top "2em"
     :position "relative"}

    [:>a.back
     {:display "inline-block"
      :font-size "1.8em"
      :text-decoration "none"
      :color colors/text-light
      :left "-1em"
      :position "absolute"}]

    [:>h1
     {:color colors/accent
      :display "inline-block"
      :font-size "1.8em"
      :margin-bottom "1rem"}]]

   [:>.items
    {:display "flex"
     :flex-direction "column"
     :flex-wrap "wrap"}

    [:>.item
     {:margin "1em 0"
      :max-width "33rem"}

     [:>img
      {:width "100%"}]

     [:>h2
      {:color colors/accent
       :font-size "1.1em"}]

     [:>p :>ul :li
      {:margin "0.5em 0"}]

     [:a
      {:color colors/accent
       :text-decoration "none"}]]]])
