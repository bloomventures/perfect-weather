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

(defn mode-filter 
  "Given a collection of boolean values,
   replaces each value with the mode of the neighbors within the window.
   Window size should be odd to avoid arbitrarily-decided ties."
  [window coll]
  (let [hw (int (/ window 2))]
    (->> 
      (concat [(repeat hw (first coll))] 
              coll 
              [(repeat hw (last coll))])
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

(defn combined-filter [coll]
  (->> coll
       ; remove 1-day false gaps
       (streak-filter false? 1)
       ; remove false gaps dominated by neighboring true streaks
       (neighbor-filter false?)
       (density-filter false? 28 0.65)
       ; bridge 5-day false gaps
       (streak-filter false? 5)
       ; only keep 28-day true streaks
       (streak-filter true? 28)))
