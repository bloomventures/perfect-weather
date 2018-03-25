(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.places :as places]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.filters :as filters]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.rate :as rate]))

(defn compute [{:keys [lat lon city country]}]
  (let [equivalent-place (or (places/closest-to {:lat lat :lon lon}) {:lat lat :lon lon})
        data (data/city-day-data {:lat (equivalent-place :lat) 
                                  :lon (equivalent-place :lon)})
        days (->> data
                  (map (fn [hours]
                         (rate/day-result? rate/nice? true hours)))
                  (filters/combined-filter))
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
     :percent percent
     :ranges ranges}))

(defn compute-by-place-id [place-id]
  (let [place @(google-maps/place place-id)]
    (compute {:lat (place :lat)
              :lon (place :lon)
              :country (place :country)
              :city (place :city)})))


(def routes
  [[:get "/api/random/:n"]
   (fn [req]
     {:status 200
      :body (->> (places/n-random (Integer/parseInt (get-in req [:params :n])))
                 (map compute))})

   [:get "/api/autocomplete"]
   (fn [req]
     {:status 200
      :body (google-maps/autocomplete (get-in req [:params :query]))})

   [:get "/api/search"]
   (fn [req]
     {:status 200
      :body (compute-by-place-id (get-in req [:params :place-id]))})
   
   [:get "/api/analysis/:n"]
   (fn [req]
     {:status 200
      :body  (->> (places/n-random (Integer/parseInt (get-in req [:params :n])))
                  (map (fn [city]
                         (assoc city
                           :data (data/city-day-data city)))))})])

