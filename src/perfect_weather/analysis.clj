(ns perfect-weather.analysis
  (:require
    [mount.core :as mount]
    [bloom.omni.figwheel] ; mount component
    [bloom.omni.http-server] ; mount component
    [bloom.omni.css-watcher] ; mount component
    [bloom.omni.spa :as spa]
    [bloom.omni.ring :as ring]
    [ring.middleware.format :refer [wrap-restful-format]]
    [perfect-weather.server.api :as api]
    [perfect-weather.analysis.styles]))

(defn start! []
  (-> (mount/with-args {:handlers [(-> (ring/->handler api/routes)
                                       (wrap-restful-format :formats [:transit-json]))
                                   (ring/->handler spa/routes)]
                        :figwheel-port 5223
                        :http-port 1262
                        :css {:styles 'perfect-weather.analysis.styles/styles}
                        :cljs {:main "perfect-weather.analysis.core"}})
      (mount/start)))

