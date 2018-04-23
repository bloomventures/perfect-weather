(ns perfect-weather.data.compute
  (:require
    [perfect-weather.data.core :as data]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.places :as places]
    [perfect-weather.data.rate :as rate]))

(defn compute [{:keys [lat lon city country]}]
  (let [equivalent-place (or (places/closest-to {:lat lat :lon lon}) 
                             {:lat lat :lon lon})
        data (data/city-day-data {:lat (equivalent-place :lat) 
                                  :lon (equivalent-place :lon)})
        days (rate/nice-days data) 
        ranges (->> (summary/ranges days)
                    (map (fn [range]
                           {:text (summary/range->text range)
                            :range range})))
        percent (int (* (/ (->> days
                                (remove false?)
                                count)
                           365)
                        100))]
    {:city city
     :country country
     :place-id (equivalent-place :place-id)
     :percent percent
     :ranges ranges}))

(defn compute-by-place-id [place-id]
  (let [place @(google-maps/place place-id)]
    (compute {:lat (place :lat)
              :lon (place :lon)
              :country (place :country)
              :city (place :city)})))

(defn compute-by-place-id-cached [])
