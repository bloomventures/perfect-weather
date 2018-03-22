(ns perfect-weather.data.summary
  (:require
    [clojure.string :as string]
    [perfect-weather.data.months :refer [months]]))

(defn first-and-last [coll]
  [(first coll) (last coll)])

(defn text 
  "Given a list of booleans corresponding to days of a year,
  returns the periods that are true"
  [days]

  (let [groups (->> days
                    ;   Jan1  Jan2
                    ; [ false false false true true false ... false true true true ... ]
                    (map-indexed (fn [i day]
                                   [i day]))
                    ; [ [0 false] [1 false] [2 true] ...]
                    (partition-by second)
                    ; [ [ [0 false] [1 false] ]
                    ;   [ [2 true] [3 true] ] ...]
                    (remove (fn [coll]
                              (false? (second (first coll)))))
                    ; [ [ [2 true] ... [6 true] ] 
                    ;   [ [22 true] ... [53 true] ]...]
                    )]
    (cond
      (and 
        (= 1 (count groups))
        (= (count days) 
           (count (first groups)))) 
      "all year"

      (= 0 (count groups)) "never"

      :else (->> groups
                 ; [ [ [2 true] ...  [6 true] ] 
                 ;   [ [22 true] ... [53 true] ]...]
                 (map first-and-last)
                 ; [ [ [2 true] [6 true] ] 
                 ;   [ [22 true] [53 true] ]...]
                 (map (fn [group]
                        (map first group)))
                 ; [ [24 68] [123 296] ... ]
                 (map (fn [range]
                        (->> range
                             ; [ 123 296 ]
                             (map (fn [day-number]
                                    #?(:cljs (js/Date. 2017 0 (inc day-number)))))
                             ; [inst-apr-13 inst-jun-25]  
                             (map (fn [date]
                                    {:month (.getMonth date)
                                     :day (.getDate date)}))
                             ; [{:month 4 :day 13} {:month 6 :day 25}]
                             (map (fn [{:keys [month day]}]
                                    (let [prefix (cond 
                                                   (< day 10) "early "
                                                   (< day 21) "mid-"
                                                   :else "late ")]
                                      (str prefix (months month)))))
                             ; ["mid-April" "late June"]
                             (string/join " to ")
                             ; "mid-April to late June"
                             )))
                 (string/join ", ")))))

(defn days-count
  [days]
  (let [c (->> days
               (remove false?)
               count)]
    (str c " nice days" " " "(" (int (* 100 (/ c (count days)))) "%" ")")))
