(ns perfect-weather.systems.prod
  (:require
    [environ.core :refer [env]]
    [mount.core :as mount]
    [perfect-weather.config :refer [config]]
    [perfect-weather.systems.base :refer [handlers]]))

(defn start! []
  (-> (mount/with-args 
        (merge config
               {:handlers handlers
                :http-port (env :http-port)}))
      (mount/start)))

