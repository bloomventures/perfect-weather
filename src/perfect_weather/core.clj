(ns perfect-weather.core
  (:gen-class)
  (:require
    [mount.core :as mount]
    [perfect-weather.analysis :as analysis]
    [perfect-weather.systems.dev :as dev]
    [perfect-weather.systems.prod :as prod]))

(defn start! [app]
  (case app
    :dev (dev/start!) 
    :analysis (analysis/start!)
    :prod (prod/start!)))

(defn stop! []
  (mount/stop))

(defn -main []
  (start! :prod))
