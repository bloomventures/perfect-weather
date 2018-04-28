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

; factors

(defn nice? [d]
  (and 
    (thermally-comfortable? d)
    (not (rainy? d))))

(defn hot? [d]
  (and 
    (d :temperature)
    (< 28 (d :temperature))
    (not (nice? d))))

(defn cold? [d]
  (and 
    (d :temperature)
    (< (d :temperature) 16)
    (not (nice? d))))

(defn humid? [d]
  (and 
    (d :humidity)
    (< 0.60 (d :humidity))
    (not (nice? d))))

(defn hot-and-humid? [d]
  (and
    (d :humidity)
    (d :temperature)
    (hot? d)
    (humid? d)))

(defn dry? [d]
  (and 
    (d :humidity)
    (< (d :humidity) 0.30)
    (not (nice? d))))

; other...

(def hour-start 9)
(def hour-end 18)
(def hour-count (- hour-end hour-start))
(def hour-threshold 5)

(defn day-factor-hours-count [f data]
  (->> data
       (drop hour-start)
       (take hour-count)
       (map f)
       (filter true?)
       count))

(defn combined-filter [f coll]
  (if (= f nice?) 
    (->> coll
         ; bridge 7-day false gaps
         (filters/streak-filter false? 7)
         ; only keep 21-day true streaks
         (filters/streak-filter true? 21))
    (->> coll
         (filters/streak-filter false? 21)
         (filters/streak-filter true? 21))))

(defn factor-day? [f day]
  (if (= f nice?) 
    (<= (/ hour-threshold hour-count) day)
    (<= (/ (- hour-count hour-threshold) hour-count) day)))

(defn years->factor-days 
  "Given multiple years of hourly data points, returns a single year of median-nice-hour-% (within the relevant hour band)"
  [f data]
  (->> data
       (partition 365)
       (apply interleave)
       (partition (/ (count data) 365))
       (map (fn [days]
              (->> days
                   (map (partial day-factor-hours-count f))
                   ((fn [day-results]
                      (min
                        ; median
                        (->> day-results
                             sort
                             second
                             ((fn [c]
                                (/ c hour-count))))
                        ; avg
                        (/ (apply + day-results)
                           (count day-results)
                           hour-count)))))))))

(defn factor-days 
  "Expects multiple years of data;
   returns single year of boolean nice/not-nice"
  [f data]
  (->> data
       (years->factor-days f)
       (filters/median-filter 7)
       (map (partial factor-day? f))
       (combined-filter f)))


