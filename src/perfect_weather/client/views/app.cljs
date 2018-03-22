(ns perfect-weather.client.views.app
  (:require
    [perfect-weather.client.views.analysis :refer [analysis-view]]))

(defn app-view []
  [analysis-view])
