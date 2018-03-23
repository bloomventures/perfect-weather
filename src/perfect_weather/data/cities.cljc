(ns perfect-weather.data.cities)

(defn key-by [k coll]
  (reduce (fn [memo i]
            (assoc memo (i k) i)) {} coll))

(def cities
  [{:key :athens 
    :city "Athens"
    :country "Greece"
    :lat 37.983972
    :lon 23.727806
    :offset 2}
   {:key :honolulu
    :city "Honolulu"
    :country "USA"
    :lat 21.3069444
    :lon -157.5853333}
   {:key :gdansk
    :city "Gdansk"
    :country "Poland"
    :lat 54.35202520000001
    :lon 18.6466384}
   {:key :kaohsiung
    :city "Kaohsiung"
    :country "Taiwan"
    :lat 22.633333
    :lon 120.266667
    :offset 8}
   {:key :lisbon
    :city "Lisbon"
    :country "Portugal"
    :lat 38.713889
    :lon -9.139444
    :offset 0}
   {:key :sanfrancisco
    :city "San Francisco"
    :country "USA"
    :lat 37.7749295
    :lon -122.4194155}
   {:key :singapore
    :city "Singapore"
    :country "Singapore"
    :lat 1.283333
    :lon 103.833333
    :offset 8}
   {:key :taipei
    :city "Taipei"
    :country "Taiwan"
    :lat 25.066667
    :lon 121.516667
    :offset 8}
   {:key :tokyo
    :city "Tokyo"
    :country "Japan"
    :lat 35.683333
    :lon 139.683333
    :offset 9}
   {:key :toronto
    :city "Toronto"
    :country "Canada"
    :lat 43.676667 
    :lon -79.630556
    :offset -5}
   {:key :warsaw
    :city "Warsaw"
    :country "Poland"
    :lat 52.233333
    :lon  21.016667
    :offset 1}
   {:key :vancouver
    :city "Vancouver"
    :country "Canada"
    :lat 49.25
    :lon -123.1
    :offset -7}])

(def cities-by-key 
  (key-by :key cities))
