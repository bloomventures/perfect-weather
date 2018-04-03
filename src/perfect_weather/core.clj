(ns perfect-weather.core
  (:require
    [mount.core :as mount]
    [perfect-weather.analysis :as analysis]
    [perfect-weather.dev :as dev]))

(defn start! [app]
  (case app
    :dev (dev/start!) 
    :analysis (analysis/start!)))

(defn stop! []
  (mount/stop))
