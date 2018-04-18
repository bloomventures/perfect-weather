(ns perfect-weather.core
  (:gen-class)
  (:require
    [mount.core :as mount]
    [environ.core :refer [env]]
    [bloom.omni.ring :as ring]
    [bloom.omni.spa :as spa]
    [bloom.omni.http-server] ; mount component
    [bloom.omni.figwheel] ; mount component
    [bloom.omni.css-watcher] ; mount component
    [ring.middleware.format :refer [wrap-restful-format]]
    [ring.middleware.defaults :refer [wrap-defaults
                                      api-defaults
                                      secure-api-defaults]]
    [perfect-weather.server.api :as api]
    [perfect-weather.config :refer [config]]))

(defn handlers []
  [(-> (ring/->handler api/routes)
       (wrap-defaults (if (= "prod" (env :environment))
                        secure-api-defaults
                        api-defaults))
       (wrap-restful-format :formats [:transit-json]))
   (ring/->handler spa/routes)])

(defn start! [app]
  (case app
    :analysis
    (-> (mount/with-args 
          (merge config
                 {:handlers (handlers)
                  :figwheel-port 5223
                  :http-port 1262
                  :css {:styles "perfect-weather.analysis.styles/styles"}
                  :cljs {:main "perfect-weather.analysis.core"}}))
        (mount/start))

    :dev
    (-> (mount/with-args 
          (merge config
                 {:handlers (handlers)
                  :figwheel-port 5223
                  :http-port 1262}))
        (mount/start))

    :prod
    (-> (mount/with-args 
          (merge config
                 {:handlers (handlers)
                  :http-port (Integer/parseInt (env :http-port))}))
        (mount/except
          #{#'bloom.omni.figwheel/component
            #'bloom.omni.css-watcher/component})
        (mount/start))))

(defn stop! []
  (mount/stop))

(defn -main [& _]
  (start! :prod))
