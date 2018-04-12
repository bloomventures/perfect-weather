(ns perfect-weather.systems.compile
  (:require
    [mount.core :as mount]
    [bloom.omni.compile] ; for mount system
    [perfect-weather.config :refer [config]]))

(defn compile! []
  (->> (mount/with-args config)
       (mount/start)))
