(ns perfect-weather.data.compute
  (:require
    [perfect-weather.data.core :as data]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.places :as places]
    [perfect-weather.data.rate :as rate]))

(def factors
  ; the order of these determines their precedence in the final result
  [[:nice rate/nice?]
   [:hot-and-humid rate/hot-and-humid?]
   [:hot rate/hot?]
   [:cold rate/cold?]
   [:humid rate/humid?]
   [:dry rate/dry?]])

(defn calc-ranges 
  [data]
  (->> factors 
       (map (fn [[k f]]
                 (->> (rate/factor-days f data)
                      (map (fn [bool]
                             (if bool k nil))))))
       (apply interleave)
       (partition (count factors))
       (map set)
       ; [ #{nil :nice :humid} ... x365 ]
       (map (fn [day]
              (->> factors
                   (map (fn [[k f]]
                          (when (contains? day k) k)))
                   (some identity))))
       ; [ :cold :cold :nice :nice ...]
       (partition-by identity)
       ; [ [:cold :cold] [:nice :nice] ...]
       (reduce (fn [memo r]
                 {:count (+ (memo :count) 
                            (count r))
                  :ranges (conj (memo :ranges)
                                {:factor (first r)
                                 :start (memo :count)
                                 :end (+ (memo :count)
                                         (count r))})}) 
               {:count 0
                :ranges []})
       :ranges
       (map (fn [r]
              [(r :factor) (r :start) (r :end) (summary/range->text [(r :start) (r :end)])]))))

(defn compute [{:keys [lat lon city country place-id]}]
  (let [equivalent-place (or (places/closest-to {:lat lat :lon lon}) 
                             {:lat lat :lon lon})
        data (data/city-day-data {:lat (equivalent-place :lat) 
                                  :lon (equivalent-place :lon)})]
    {:city city
     :country country
     :place-id (or place-id (equivalent-place :place-id))
     :ranges (calc-ranges data)}))

(defn compute-by-place-id [place-id]
  (let [place @(google-maps/place place-id)]
    (compute {:lat (place :lat)
              :lon (place :lon)
              :country (place :country)
              :city (place :city)
              :place-id place-id})))

(defn compute-by-place-id-cached [])
