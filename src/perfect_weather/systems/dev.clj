(ns perfect-weather.systems.dev
  (:require
    [mount.core :as mount]
    [bloom.omni.figwheel] ; mount component
    [bloom.omni.css-watcher] ; mount component
    [perfect-weather.config :refer [config]]
    [perfect-weather.systems.base :refer [handlers]]))

(defn start! []
  (-> (mount/with-args 
        (merge config
               {:handlers handlers
                :figwheel-port 5223
                :http-port 1262}))
      (mount/start)))

