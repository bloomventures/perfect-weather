(ns perfect-weather.data.wunderground
  (:require
    [clojure.string :as string]
    [clj-time.core :as t]
    [clj-time.coerce :as c]
    [perfect-weather.data.cache :refer [with-cache]]
    [perfect-weather.data.http :as http]))

; https://www.wunderground.com/weather/api/d/docs?d=data/history

(defn fetch 
  [{:keys [api-key lat lon ymd]}]
  ; could also do country/city instead of lat,lon
  (http/fetch 
    {:url (str "http://api.wunderground.com/api/"
               api-key 
               "/history_" ymd 
               "/q/" 
               lat "," lon 
               ".json")}))

(defn fetch-day-history
  [{:keys [api-key lat lon ymd]}]
  (future
    (->> (with-cache 
           :wunderground
           [(str lat " " lon) ymd]
           fetch
           {:api-key api-key
            :lat lat
            :lon lon
            :ymd ymd})
         deref
         :history
         :observations
         (map (fn [o]
                {:epoch (->> (t/date-time 
                               (Integer. (get-in o [:utcdate :year]))
                               (Integer. (get-in o [:utcdate :mon]))
                               (Integer. (get-in o [:utcdate :mday])))
                             (c/to-epoch))
                 :humidity (if (= "N/A" (o :hum))
                             nil
                             (float (/ (Integer. (o :hum)) 100)))
                 :temperature (Float. (o :tempm))
                 :precipitation? (or (= "1" (o :rain))
                                     (= "1" (o :snow))
                                     (= "1" (o :hail)))})))))


