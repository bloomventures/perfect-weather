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
   replaces each value with the mode of the neighbors within the window"
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
  flips f-truthy streaks where both neigbhoring streaks are larger"
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
