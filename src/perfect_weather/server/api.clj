(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.cities :refer [cities]]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.filters :as filters]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.rate :as rate]))

(defn compute [city]
  (let [data (data/city-day-data {:lat (city :lat)
                                  :lon (city :lon)})
        days (->> data
                  (map (fn [hours]
                         (rate/day-result? rate/nice? true hours)))
                  (filters/combined-filter))
        groups (->> (summary/ranges days)
                    (map (fn [range]
                           {:text (summary/range->text range)
                            :range range})))
        percent (int (* (/ (->> days
                                (remove false?)
                                count)
                           365)
                        100))]
    {:city (name (city :key))
     :country "Someplace"
     :percent percent
     :groups groups}))

(def routes
  [[:get "/api/random/:n"]
   (fn [req]
     {:status 200
      :body (->> (take 3 (shuffle cities))
                 (map compute))})
   
   [:get "/api/data"]
   (fn [_]
     {:status 200
      :body (data/all)})])

