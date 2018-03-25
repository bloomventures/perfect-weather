(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.places :as places]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.filters :as filters]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.rate :as rate]))

(defn distance
  [{lon1 :lon lat1 :lat} {lon2 :lon lat2 :lat}]
  (let [R 6378.137 ; Radius of Earth in km
        dlat (Math/toRadians (- lat2 lat1))
        dlon (Math/toRadians (- lon2 lon1))
        lat1 (Math/toRadians lat1)
        lat2 (Math/toRadians lat2)
        a (+ (* (Math/sin (/ dlat 2)) (Math/sin (/ dlat 2))) (* (Math/sin (/ dlon 2)) (Math/sin (/ dlon 2)) (Math/cos lat1) (Math/cos lat2)))]
    (* R 2 (Math/asin (Math/sqrt a)))))

(defn compute [{:keys [lat lon city country]}]
  (let [data (data/city-day-data {:lat lat 
                                  :lon lon})
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
  (let [place (google-maps/place-details place-id)
        city (->> (places/all)
                  (map (fn [{:keys [lat lon] :as city}]
                         [city (distance city place)]))
                  (remove (fn [[_ dist]]
                            (> dist 100)))
                  first
                  first)]
    (if city
      (compute {:lat (city :lat)
                :lon (city :lon)
                :country (place :country)
                :city (place :city)})
      (compute {:lat (place :lat)
                :lon (place :lon)
                :country (place :country)
                :city (place :city)}))))

(def routes
  [[:get "/api/random/:n"]
   (fn [req]
     {:status 200
      :body (->> (places/n-random 3)
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

