(ns perfect-weather.client.ui.app-styles
  (:require
    [garden.stylesheet :refer [at-import]]
    [perfect-weather.client.ui.faq-page-styles :refer [>faq-page]]
    [perfect-weather.client.ui.index-page-styles :refer [>index-page]]))

(defn styles []
  [(at-import "https://fonts.googleapis.com/css?family=Montserrat:400,600")
   [:body :h1 :h2 :p
    {:margin 0
     :padding 0}]

   [:#app
    {:font-family "Montserrat"}

    [:>.app
     {:display "flex"
      :min-height "100vh"
      :flex-direction "column"
      :justify-content "center"
      :align-items "center"}

     (>index-page)
     (>faq-page)]]])
