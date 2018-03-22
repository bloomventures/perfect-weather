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

(defn nice? 
  "Polygon:
    
     . 17t,85h 
           . 27t,75h

     .         .  32t,20h
       17t,20h
  
  References:
https://en.wikipedia.org/wiki/Thermal_comfort
https://www.educate-sustainability.eu/kb/content/factors-comfort
http://saroselectronics.com/digital-relative-humidity-display/
  "
  [d]
  (let [t (d :temperature)
        h (d :humidity)
        pts [[17 0.85]
             [17 0.20]
             [32 0.20]
             [27 0.75]]]
    (and
      (<= 17 t 32)
      (<= 0.20 h 0.85)
      (within-polygon? [t h] pts))))

(defn issue [d]
  (cond 
    (hot? d) :hot
    (cold? d) :cold
    (humid? d) :humid
    (dry? d) :dry
    (perfect? d) :perfect
    (nice? d) :nice))
