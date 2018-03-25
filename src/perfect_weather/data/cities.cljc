(ns perfect-weather.data.cities)

(defn key-by [k coll]
  (reduce (fn [memo i]
            (assoc memo (i k) i)) {} coll))

(def cities
  [{:key :athens 
    :city "Athens"
    :country "Greece"
    :lat 37.98
    :lon 23.73
    :offset 2}
   {:key :honolulu
    :city "Honolulu"
    :country "USA"
    :lat 21.31
    :lon -157.59}
   {:key :gdansk
    :city "Gdansk"
    :country "Poland"
    :lat 54.35
    :lon 18.65}
   {:key :kaohsiung
    :city "Kaohsiung"
    :country "Taiwan"
    :lat 22.63
    :lon 120.27
    :offset 8}
   {:key :lisbon
    :city "Lisbon"
    :country "Portugal"
    :lat 38.71
    :lon -9.14
    :offset 0}
   {:key :sanfrancisco
    :city "San Francisco"
    :country "USA"
    :lat 37.77
    :lon -122.42}
   {:key :singapore
    :city "Singapore"
    :country "Singapore"
    :lat 1.28
    :lon 103.83
    :offset 8}
   {:key :taipei
    :city "Taipei"
    :country "Taiwan"
    :lat 25.07
    :lon 121.52
    :offset 8}
   {:key :tokyo
    :city "Tokyo"
    :country "Japan"
    :lat 35.68
    :lon 139.68
    :offset 9}
   {:key :toronto
    :city "Toronto"
    :country "Canada"
    :lat 43.68
    :lon -79.63
    :offset -5}
   {:key :warsaw
    :city "Warsaw"
    :country "Poland"
    :lat 52.23
    :lon  21.02
    :offset 1}
   {:key :vancouver
    :city "Vancouver"
    :country "Canada"
    :lat 49.25
    :lon -123.10
    :offset -7}])

(def cities-by-key 
  (key-by :key cities))
