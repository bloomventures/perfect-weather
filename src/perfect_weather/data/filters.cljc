(ns perfect-weather.data.filters)

(defn- mode 
  "Given a collection, return the value of the most common item.
  
  If there is a tie, one is chosen arbitrarily."
  [coll]
  (->> coll
       frequencies
       (sort-by (fn [[_ cnt]] cnt))
       reverse
       first
       first))

(defn median
  [coll]
  (let [sorted (sort coll)]
    (if (even? (count coll))
      (/ (+ (nth sorted (dec (int (/ (count coll) 2))))
            (nth sorted (int (/ (count coll) 2))))
         2)
      (nth sorted (int (/ (count coll) 2))))))

(defn mode-filter 
  "Given a collection of boolean values,
   replaces each value with the mode of the neighbors within the window.
   Window size should be odd to avoid arbitrarily-decided ties."
  [window coll]
  (let [hw (int (/ window 2))]
    (->> 
      (concat (repeat hw (first coll)) 
              coll 
              (repeat hw (last coll)))
      (partition window 1)
      (map mode))))

(defn streak-filter 
  "Given a collection of boolean values,
  flips f-truthy streaks of size < n"
  [f n coll]
  (->> coll
       (partition-by identity)
       (map (fn [i]
              (let [c (count i)
                    v (first i)]
                (cond
                  (and (f v) (<= c n))
                  (repeat c (not v)) 

                  :else
                  i))))
       flatten))

(defn neighbor-filter 
  "Given a collection of boolean values
  flips f-truthy streaks where both neighboring streaks are larger"
  [f coll]
  (let [partitions (partition-by identity coll)]
    (->> (concat [(first partitions)] partitions [(last partitions)])
         (partition 3 1)
         (map (fn [[pre i post]]
                (let [c (count i)
                      v (first i)]
                  (cond
                    (and 
                      (f v)
                      (and 
                        (< (count i) (count pre))
                        (< (count i) (count post))))
                    (repeat c (not v)) 

                    :else
                    i))))
         flatten)))

(defn density-filter 
  "Given a collection of boolean values
  flips f-truthy value where density of f-falsey values in window around the current value 
  is greater than the given density.
  
  Window size should be odd to avoid arbitrarily-decided ties."
  [f window density coll]
  (let [hw (int (/ window 2))]
    (->> (concat (repeat hw (first coll)) 
                 coll 
                 (repeat hw (last coll)))
         (partition window 1)
         (map (fn [range]
                (let [mid (nth range hw)
                      [top top-count] (->> range
                                           frequencies
                                           (sort-by second)
                                           reverse
                                           first)]
                  (if (and (f mid) (> (/ top-count (count range)) density))
                    top
                    mid)))))))

(defn weighted-moving-average
  "Calculates a weighted moving average given a coll and weights
   Weights should be odd, and add up to 1. Loops around for beginning / end values.
  Effectively, a convolution."
  [coll weights]
  (let [window (count weights)
        loopw (dec (/ window 2))
        coll (concat (take-last loopw coll) 
                     coll
                     (take loopw coll))]
    (->> coll
         (partition window 1)
         (map (fn [x]
                (reduce + (map * x weights)))))))


(defn gaussian-filter-numbers 
  "Given a collection of numbers, applies a gaussian filter / performs a weirstrauss transform 
  for the given standard deviation"
  [coll]
  (weighted-moving-average coll [0.0545 0.2442 0.4026 0.2442 0.0545]))

(defn median-filter
  "Given a collection of numbers, transforms each value to be the median of the values in the window around that value.
  
  Window size should be odd. Loops around for beginning / end values."
  [window coll]
  (let [hw (int (/ window 2))]
    (->> 
      (concat (take-last hw coll) 
              coll 
              (take hw coll))
      (partition window 1)
      (map median))))

