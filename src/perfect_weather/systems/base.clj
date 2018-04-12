(ns perfect-weather.systems.base
  (:require
    [bloom.omni.ring :as ring]
    [bloom.omni.spa :as spa]
    [bloom.omni.http-server] ; mount component
    [ring.middleware.defaults :refer [wrap-defaults
                                      api-defaults
                                      secure-api-defaults]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [perfect-weather.server.api :as api]))

(def handlers
  [(-> (ring/->handler api/routes)
       (wrap-defaults api-defaults)
       (wrap-restful-format :formats [:transit-json]))
   (ring/->handler spa/routes)])
