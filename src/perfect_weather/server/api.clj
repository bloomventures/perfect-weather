(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.core :as data]))

(def routes
  [[:get "/api/data"]
   (fn [_]
     {:status 200
      :body (data/all)})])

