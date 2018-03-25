(ns perfect-weather.data.places
  (:require
    [clojure.java.io :as io]
    [perfect-weather.data.google-maps :as google-maps]))

(defn all []
  (->> (io/file "data/places")
       file-seq 
       (filter #(.isFile %))
       (map (comp read-string slurp))))

(defn n-random [n]
  (->> (io/file "data/places")
       file-seq 
       (filter #(.isFile %))
       shuffle
       (take n)
       (map (comp read-string slurp))))

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

(defn closest-to 
  "Returns closest place that is within threshold"
  [{:keys [lat lon] :as needle}]
  (let [threshold 100]
    (->> (all)
         (map (fn [{:keys [lat lon] :as city}]
                [city (distance city needle)]))
         (remove (fn [[_ dist]]
                   (> dist threshold)))
         first
         first)))
