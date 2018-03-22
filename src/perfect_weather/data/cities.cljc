(ns perfect-weather.data.cities)

(defn key-by [k coll]
  (reduce (fn [memo i]
            (assoc memo (i k) i)) {} coll))

(def cities
  [{:key :toronto
    :lat 43.676667 
    :lon -79.630556
    :offset -5}
   {:key :warsaw
    :lat 52.233333
    :lon  21.016667
    :offset 1}
   {:key :tokyo
    :lat 35.683333
    :lon 139.683333
    :offset 9}
   {:key :vancouver
    :lat 49.25
    :lon -123.1
    :offset -7}
   {:key :athens 
    :lat 37.983972
    :lon 23.727806
    :offset 2}
   {:key :singapore
    :lat 1.283333
    :lon 103.833333
    :offset 8}
   {:key :taipei
    :lat 25.066667
    :lon 121.516667
    :offset 8}
   {:key :kaohsiung
    :lat 22.633333
    :lon 120.266667
    :offset 8}
   {:key :lisbon
    :lat 38.713889
    :lon -9.139444
    :offset 0}])

(def cities-by-key 
  (key-by :key cities))
