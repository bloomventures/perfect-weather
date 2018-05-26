(ns perfect-weather.data.core
  (:require
    [bloom.omni.env :as env]
    [clj-time.core :as t]
    [clj-time.periodic :as p]
    [clj-time.format :as f]
    [perfect-weather.data.places :as places]
    [perfect-weather.data.darksky :as darksky]
    [perfect-weather.data.wunderground :as wunderground]))

(defn fetch-day-history
  "For given lat, lon, ymd, 
   returns array of maps:
    {:epoch _
     :temperature _
     :humidity _}"
  [{:keys [provider lat lon ymd api-key]}]
  (let [[f api-key] 
        (case provider
          :darksky 
          [darksky/fetch-day-history (env/get :darksky-api-key)]
          :wunderground 
          [wunderground/fetch-day-history (env/get :wunderground-api-key)])]
    (f {:lat lat
        :lon lon
        :ymd ymd
        :api-key api-key}))) 

(defn city-day-data 
  [{:keys [lat lon]}]
  (let [ymds (->> (p/periodic-seq (t/date-time 2015 01 01) (t/hours 24))
                  (take (* 3 365))
                  (map (fn [date]
                         (f/unparse (f/formatter "yyyyMMdd") date))))]
    (->> ymds
         (map (fn [ymd]
                (fetch-day-history 
                  {:provider :darksky
                   :lat lat 
                   :lon lon 
                   :ymd ymd})))
         doall
         (map deref)
         doall)))
