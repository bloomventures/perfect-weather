(ns perfect-weather.core
  (:require
    [perfect-weather.analysis :as analysis]
    [perfect-weather.dev :as dev]))

(defn start-analysis! []
  (analysis/start!))

(defn start-dev! []
  (dev/start!))
