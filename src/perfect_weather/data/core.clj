(ns perfect-weather.data.core
  (:require
    [environ.core :refer [env]]
    [clj-time.core :as t]
    [clj-time.periodic :as p]
    [clj-time.format :as f]
    [perfect-weather.data.cities :refer [cities]]
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
          [darksky/fetch-day-history (env :darksky-api-key)]
          :wunderground 
          [wunderground/fetch-day-history (env :wunderground-api-key)])]
    (f {:lat lat
        :lon lon
        :ymd ymd
        :api-key api-key}))) 

(defn all []
  (let [ymds (->> (p/periodic-seq (t/date-time 2017 01 01) (t/hours 24))
                  (take 365)
                  (map (fn [date]
                         (f/unparse (f/formatter "yyyyMMdd") date))))]
    (->> cities
         (map (fn [city]
                [(city :key)
                 (->> ymds
                      (map (fn [ymd]
                             (fetch-day-history 
                               {:provider :darksky
                                :lat (city :lat)
                                :lon (city :lon)
                                :ymd ymd})))
                      doall
                      (map deref)
                      doall)])) 
         (into {}))))

(defn city-day-data 
  [{:keys [lat lon]}]
  (let [ymds (->> (p/periodic-seq (t/date-time 2017 01 01) (t/hours 24))
                  (take 365)
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
