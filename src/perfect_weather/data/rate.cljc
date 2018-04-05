(ns perfect-weather.data.rate)

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
  (< 0.85 (d :humidity)))

(defn dry? [d]
  (< (d :humidity) 0.20))

(defn perfect? [d]
  (and 
    (< 20 (d :temperature) 25) 
    (< 0.35 (d :humidity) 0.55)))

(def hour-start 8)
(def hour-end 20)
(def hour-count (- hour-end hour-start))
(def hour-threshold 8)

(defn day-result? [f p? data]
  (let [threshold (if p? hour-threshold 6)]
    (<= threshold (->> data
                       (drop hour-start)
                       (take hour-count)
                       (map f)
                       (filter true?)
                       count))))

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
  (let [t (d :temperature)
        h (d :humidity)
        pts [[16 0.80]
             [14 0.30]
             [32 0.20]
             [28 0.60]]
        max-t (->> pts (map first) (apply max))
        min-t (->> pts (map first) (apply min))
        max-h (->> pts (map second) (apply max))
        min-h (->> pts (map second) (apply min))]
    (and
      t
      h
      (<= min-t t max-t)
      (<= min-h h max-h)
      (within-polygon? [t h] pts))))

(defn rainy? [d]
  (= true (d :precipitation?)))

(defn nice? [d]
  (and 
    (thermally-comfortable? d)
    (not (rainy? d))))

(defn issue [d]
  (cond 
    (hot? d) :hot
    (cold? d) :cold
    (humid? d) :humid
    (dry? d) :dry
    (perfect? d) :perfect
    (nice? d) :nice))
