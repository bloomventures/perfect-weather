(ns perfect-weather.compile
  (:require
    [mount.core :as mount]
    [bloom.omni.compile] ; for mount system
    [perfect-weather.config :refer [config]]))

(defn compile! []
  (->> (mount/with-args config)
       (mount/only #{#'bloom.omni.impl.config/config
                     #'bloom.omni.impl.builds/builds
                     #'bloom.omni.compile/compile-css!
                     #'bloom.omni.compile/compile-js!})
       (mount/start)))
