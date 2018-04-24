(ns perfect-weather.data.rate
  (:require
    [perfect-weather.data.filters :as filters]))

(defn within-polygon?
  "Based on: 
  https://github.com/Factual/geo/blob/master/src/geo/poly.clj#L8"
  [[x y] points]
  (loop [[[x1 y1] & others] points
         [x2 y2] (last points)
         inside? false]
    (let [crosses? (not= (> x1 x) (> x2 x))
          intersects? (and crosses?
                        (< y (+ y1 (* (- y2 y1) (/ (- x x1) (- x2 x1))))))
          inside?' (if intersects? (not inside?) inside?)]
      (if (empty? others)
        inside?'
        (recur others [x1 y1] inside?')))))

(defn hot? [d]
  (< 32 (d :temperature)))

(defn cold? [d]
  (< (d :temperature) 17))

(defn humid? [d]
  (< 0.75 (d :humidity)))

(defn dry? [d]
  (< (d :humidity) 0.20))

(defn perfect? [d]
  (and 
    (< 20 (d :temperature) 25) 
    (< 0.35 (d :humidity) 0.55)))
(defn fast-within-polygon?
  [[x y] points]
  (when (and x y)
    (let [max-x (->> points (map first) (apply max))
          min-x (->> points (map first) (apply min))]
      (when (<= min-x x max-x)
        (let [max-y (->> points (map second) (apply max))
              min-y (->> points (map second) (apply min))]
          (when (<= min-y y max-y)
            (within-polygon? [x y] points)))))))

(defn thermally-comfortable? 
  "Polygon:
    
       . 16t,80h 

                    . 28t,60h

     . 14t,30h
                        .  32t,20h
  
  References:
    https://en.wikipedia.org/wiki/Thermal_comfort
    https://en.wikipedia.org/wiki/Relative_humidity
    https://en.wikipedia.org/wiki/Dew_point
    https://en.wikipedia.org/wiki/Humidex
    https://en.wikipedia.org/wiki/Heat_index"
  [d]
  (fast-within-polygon? [(d :temperature) (d :humidity)]
                        [[16 0.80]
                         [14 0.30]
                         [32 0.20]
                         [28 0.60]]))

(defn rainy? [d]
  (= true (d :precipitation?)))

(defn nice? [d]
  (and 
    (thermally-comfortable? d)
    (not (rainy? d))))

(defn issue [d]
  (->> [(when (hot? d) :hot)
        (when (cold? d) :cold)
        (when (humid? d) :humid)
        (when (dry? d) :dry)
        (when (rainy? d) :rainy)
        (when (nice? d)) :nice]
    (filter nil?)
    set))

(def hour-start 8)
(def hour-end 20)
(def hour-count (- hour-end hour-start))
(def hour-threshold 6)

(defn day-nice-hours-count [data]
  (->> data
       (drop hour-start)
       (take hour-count)
       (map nice?)
       (filter true?)
       count))

(defn combined-filter [coll]
  (->> coll
       ; bridge 10-day false gaps
       (filters/streak-filter false? 10)
       ; only keep 21-day true streaks
       (filters/streak-filter true? 21)))

(defn years->median-nice-days 
  "Given multiple years of hourly data points, returns a single year of median-nice-hour-% (within the relevant hour band)"
  [data]
  (->> data
       (partition 365)
       (apply interleave)
       (partition (/ (count data) 365))
       (map (fn [days]
              (->> days
                   (map day-nice-hours-count)
                   ((fn [day-results]
                      (->> day-results
                           sort
                           second
                           ((fn [c]
                              (/ c hour-count)))))))))))

(defn nice-days 
  "Expects multiple years of data;
   returns single year of boolean nice/not-nice"
  [data]
  (->> data
       years->median-nice-days
       (filters/median-filter 7)
       (map (fn [day]
              (<= (/ hour-threshold hour-count) day)))
       combined-filter))
