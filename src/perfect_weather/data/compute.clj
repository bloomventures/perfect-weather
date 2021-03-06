(ns perfect-weather.data.compute
  (:require
    [perfect-weather.data.cache :as cache :refer [with-cache]]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.places :as places]
    [perfect-weather.data.rate :as rate]))

(defn calc-ranges 
  [data]
  (->> rate/factors 
       (map (fn [[k f]]
                 (->> (rate/factor-days f data)
                      (map (fn [bool]
                             (if bool k nil))))))
       (apply interleave)
       (partition (count rate/factors))
       (map set)
       ; [ #{nil :nice :humid} ... x365 ]
       (map (fn [day]
              (->> rate/factors
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
       ; due to precedence, some ranges might have become clippedv
       ; and fall below the relevant length threshold
       ; remove those ranges (by marking as :null)
       (map (fn [r]
              (if (< (- (r :end) (r :start)) 5)
                (assoc r :factor :null)
                r)))
       (map (fn [r]
              [(r :factor) (r :start) (r :end) (summary/range->text [(r :start) (r :end)])]))))

(defn by-lat-lon-raw [{:keys [lat lon city country place-id]}]
  (let [equivalent-coords (or (places/equivalent-coords {:lat lat :lon lon}) 
                              {:lat lat :lon lon})
        data (data/city-day-data {:lat (equivalent-coords :lat) 
                                  :lon (equivalent-coords :lon)})]
    (when data
      {:city city
       :country country
       :lat lat
       :lon lon
       :place-id place-id
       :ranges (calc-ranges data)})))

(defn by-lat-lon [{:keys [lat lon city country place-id] :as args}]
  (->>
    (with-cache
      :results
      [place-id]
      (fn [& args]
        (future (apply by-lat-lon-raw args)))
      args)
    deref))

(defn n-random [n]
  (cache/n-random :results n))

(defn all []
  (cache/all :results))

(defn by-place-id [place-id]
  (let [place @(google-maps/place place-id)]
    (by-lat-lon {:lat (place :lat)
                 :lon (place :lon)
                 :country (place :country)
                 :city (place :city)
                 :place-id place-id})))

(defn by-autocomplete [query]
  (->> (google-maps/autocomplete query)
       first
       :place-id
       by-place-id))
