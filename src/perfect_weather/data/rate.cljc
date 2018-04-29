(ns perfect-weather.data.rate
  (:require
    [perfect-weather.data.filters :as filters]))

(defn within-polygon?
  "Polygon points must be counter-clockwise
  Based on: 
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

; References:
;  https://en.wikipedia.org/wiki/Thermal_comfort
;  https://en.wikipedia.org/wiki/Relative_humidity
;  https://en.wikipedia.org/wiki/Dew_point
;  https://en.wikipedia.org/wiki/Humidex
;  https://en.wikipedia.org/wiki/Heat_index

(defn warm? 
  [d]
  (fast-within-polygon? [(d :temperature) (d :humidity)]
                        [[19 0.85]
                         [20 0.20]
                         [32 0.20]
                         [25 0.85]]))

(defn cool? 
  [d]
  (fast-within-polygon? [(d :temperature) (d :humidity)]
                        [[16 0.85]
                         [15 0.20]
                         [20 0.20]
                         [19 0.85]]))

(defn nice? [d]
  (or (cool? d)
      (warm? d)))

(defn rainy? [d]
  (= true (d :precipitation?)))

(defn hot? [d]
  (and 
    (d :temperature)
    (< 25 (d :temperature))
    (not (nice? d))))

(defn cold? [d]
  (and 
    (d :temperature)
    (< (d :temperature) 16)
    (not (nice? d))))

(defn humid? [d]
  (and 
    (d :humidity)
    (< 0.85 (d :humidity))
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
    (< (d :humidity) 0.20)
    (not (nice? d))))

(def hour-start 8)
(def hour-end 20)
(def hour-count (- hour-end hour-start))
(def hour-threshold 7) ; not used anymore, except in docs

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
         (filters/streak-filter false? 14)
         ; only keep 21-day true streaks
         (filters/streak-filter true? 14))
    (->> coll
         (filters/streak-filter false? 14)
         (filters/streak-filter true? 14))))

(def factors
  ; the order of these determines their precedence in the final result
  ; [label fn %-threshold]
  [[:warm warm? 0.6]
   ; using nice? here on purpose; it is warm+cool
   ; leads to better transitions
   [:cool nice? 0.6] 
   ; set a low threshold for these so that 'not-nice' periods 
   ; are near-guaranteed to have a label 
   [:hot hot? 0.1]
   [:cold cold? 0.1]
   [:dry dry? 0.1]
   [:rainy rainy? 0.1]
   [:humid humid? 0.1]
   ; catch-all
   [:null any? 0]])

(defn threshold [f]
  (->> factors
       (filter (fn [[_ f' _]]
                 (= f f')))
       first
       last))

(defn factor-day? [f day]
  (<= (threshold f) day))

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
                        ; min
                        #_(/ (apply min day-results)
                           hour-count)
                        ; median
                        (->> day-results
                             sort
                             second
                             ((fn [c]
                                (/ c hour-count))))
                        ; mean
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

