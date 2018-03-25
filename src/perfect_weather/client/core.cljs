(ns perfect-weather.client.core
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch-sync]]
    [perfect-weather.client.state.subs]
    [perfect-weather.client.state.events]
    [perfect-weather.client.ui.app :refer [app-view]]))

(enable-console-print!)

(defn render []
  (r/render-component [app-view]
    (.. js/document (getElementById "app"))))

(defn ^:export init []
  (dispatch-sync [:init!])
  (render))

(defn ^:export reload []
  (render))
