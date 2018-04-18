(ns perfect-weather.client.ui.app-styles
  (:require
    [garden.stylesheet :refer [at-import at-keyframes]]
    [perfect-weather.client.ui.faq-page-styles :refer [>faq-page]]
    [perfect-weather.client.ui.index-page-styles :refer [>index-page]]))

(defn styles []
  [(at-import "https://fonts.googleapis.com/css?family=Montserrat:400,600")

   (at-keyframes "throb"
     ["0%" {:transform "scale(1)"}]
     ["50%" {:transform "scale(0.9)"}]
     ["100%" {:transform "scale(1)"}])

   (at-keyframes "spin"
     ["0%" {:transform "rotate(0deg)"}]
     ["100%" {:transform "rotate(359deg)"}])

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
