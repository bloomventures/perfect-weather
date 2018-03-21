(ns perfect-weather.data.darksky
  (:require
    [clojure.string :as string]
    [clj-time.coerce :as c]
    [clj-time.format :as f]
    [perfect-weather.data.http :as http]
    [perfect-weather.data.cache :refer [with-cache]]))

; https://darksky.net/dev/
 
(defn fetch
  [{:keys [api-key lat lon ymd]}]
  (let [time (c/to-epoch (f/parse (f/formatter "yyyyMMdd") ymd))
        base-url "https://api.darksky.net/forecast"
        url (string/join "/" [base-url api-key 
                              (->> [lat lon time]
                                   (map str)
                                   (filter not-empty)
                                   (string/join ","))])]
    (http/fetch {:url url
                 :params {:exclude "daily"
                          :units "ca"}})))

(defn fetch-day-history
  [{:keys [api-key lat lon ymd]}]
  (->> (with-cache 
         fetch
         {:api :darksky
          :api-key api-key
          :lat lat
          :lon lon
          :ymd ymd})
       :hourly
       :data
       (map (fn [o]
              {:epoch (o :time)
               :temperature (o :temperature)
               :humidity (o :humidity)
               :precipitation? (or (and 
                                     (o :precipProbability)
                                     (> (o :precipProbability) 0))
                                   (o :precipIntensity))}))))
