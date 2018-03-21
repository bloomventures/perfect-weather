(ns perfect-weather.core
  (:require
    [perfect-weather.dev :as dev]))

(defn start-dev! []
  (dev/start!))
