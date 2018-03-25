(ns perfect-weather.data.places
  (:require
    [clojure.java.io :as io]))

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
