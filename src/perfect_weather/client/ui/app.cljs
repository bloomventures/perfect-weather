(ns perfect-weather.client.ui.app
  (:require
    [re-frame.core :refer [subscribe]]
    [perfect-weather.client.ui.index-page :refer [index-page-view]]
    [perfect-weather.client.ui.map-page :refer [map-page-view]]
    [perfect-weather.client.ui.faq-page :refer [faq-page-view]]))

(defn app-view []
  [:div.app
   (case @(subscribe [:page])
     :index
     [index-page-view]
     :faq
     [faq-page-view]
     :map
     [map-page-view]
     nil)])
