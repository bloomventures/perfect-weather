(ns perfect-weather.core
  (:gen-class)
  (:require
    [bloom.omni.core :as omni]
    [perfect-weather.server.api :as api]))

(def analysis-config
  {:omni/api-routes api/routes
   :omni/css {:styles "perfect-weather.analysis.styles/styles"}
   :omni/cljs {:main "perfect-weather.analysis.core"}})

(def site-config
  {:omni/title "Best weather in..."
   :omni/api-routes api/routes
   :omni/css {:styles "perfect-weather.client.ui.app-styles/styles"}
   :omni/cljs {:main "perfect-weather.client.core"}})

(defn start! [app]
  (omni/start! omni/system 
               (case app
                 :analysis
                 analysis-config
                 :site
                 site-config)))

(defn stop! []
  (omni/stop!))

(defn -main [& _]
  (start! :site))





