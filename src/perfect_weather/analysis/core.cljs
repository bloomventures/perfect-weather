(ns perfect-weather.analysis.core
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [dispatch-sync]]
    [perfect-weather.analysis.state.subs]
    [perfect-weather.analysis.state.events]
    [perfect-weather.analysis.views.app :refer [app-view]]))

(enable-console-print!)

(defn render []
  (r/render-component [app-view]
    (.. js/document (getElementById "app"))))

(defn ^:export init []
  (dispatch-sync [:init!])
  (render))

(defn ^:export reload []
  (render))
