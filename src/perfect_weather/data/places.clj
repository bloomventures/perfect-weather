(ns perfect-weather.data.places
  (:require
    [clojure.java.io :as io]
    [perfect-weather.data.cache :as cache]
    [perfect-weather.data.google-maps :as google-maps]))

(defn all []
  (cache/all :places))

(defn by-city [city]
  (->> (all) 
       (filter (fn [p]
                 (= (p :city) city)))))

(defn n-random [n]
  (cache/n-random :places n))

(defn distance
  [{lon1 :lon lat1 :lat} {lon2 :lon lat2 :lat}]
  (if (and (= lon1 lon2) (= lat1 lon2))
    0
    (let [R 6378.137 ; Radius of Earth in km
          dlat (Math/toRadians (- lat2 lat1))
          dlon (Math/toRadians (- lon2 lon1))
          lat1 (Math/toRadians lat1)
          lat2 (Math/toRadians lat2)
          a (+ (* (Math/sin (/ dlat 2)) (Math/sin (/ dlat 2))) (* (Math/sin (/ dlon 2)) (Math/sin (/ dlon 2)) (Math/cos lat1) (Math/cos lat2)))]
      (* R 2 (Math/asin (Math/sqrt a))))))

(defn known-lat-lons []
  (->> (cache/cache-list-top :darksky)
       (map (fn [f]
              (let [[_ lat lon] (re-matches #"(.*) (.*)" (.getName f))]
                {:lat (Float/parseFloat lat)
                 :lon (Float/parseFloat lon)})))))

(defn equivalent-coords 
  "Returns closest {:lat _ :lon _} for which data is known (within a threshold)"
  [{:keys [lat lon] :as needle}]
  (let [threshold 100]
    (->> (known-lat-lons)
         (map (fn [{:keys [lat lon] :as coords}]
                [coords (distance coords needle)]))
         (remove (fn [[_ dist]]
                   (> dist threshold)))
         (sort-by (fn [[_ dist]]
                    dist))
         first
         first)))
