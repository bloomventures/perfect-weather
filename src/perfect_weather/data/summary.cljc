(ns perfect-weather.data.summary
  (:require
    [clojure.string :as string]
    [perfect-weather.data.months :refer [months]]
    #?(:clj [clj-time.core :as t])))

(defn first-and-last [coll]
  [(first coll) (last coll)])

(defn ranges 
  "Given collection of booleans, return indices of [[start end] ...] for each true streak" 
  [days]
  (or (some->> days
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
               seq
               ; [ [ [2 true] ... [6 true] ] 
               ;   [ [22 true] ... [53 true] ]...]
               (map first-and-last)
               ; [ [ [2 true] [6 true] ] 
               ;   [ [22 true] [53 true] ]...]
               (map (fn [group]
                      (map first group)))
               ; [ [24 68] [123 296] ... ]
               (map vec))
      []))

(defn range->text 
  [[start end]]
  (cond
    (and (= 0 start) (= 365 end))
    "all year"

    :else 
    (->> [start end]
         ; [ 123 296 ]
         (map (fn [day-number]
                #?(:cljs 
                   (let [date (js/Date. 2017 0 (inc day-number))]
                     {:month (.getMonth date)
                      :day (.getDate date)
                      :days 31})
                   :clj
                   (let [date (t/plus (t/date-time 2017 1 1) (t/days day-number))]
                     {:month (dec (t/month date))
                      :day (t/day date)
                      :days (t/number-of-days-in-the-month date)}))))
         ; [{:month 4 :day 13} {:month 6 :day 25}]
         (map (fn [{:keys [month day days]}]
                (let [percent (/ day days)
                      prefix (cond 
                               (<= percent 0.1) "start of "
                               (<= percent 0.3) "early "
                               (<= percent 0.6) "mid-"
                               (<= percent 0.9) "late "
                               (<= percent 1.0) "end of ")]
                  (str prefix (months month)))))
         ; ["mid-April" "late June"]
         (string/join " to ")
         ; "mid-April to late June"
         )))

(defn text 
  "Given a list of booleans corresponding to days of a year,
  returns the periods that are true"
  [days]
  (->> (ranges days)
       (map range->text)
       (string/join ", ")))

(defn days-count
  [days]
  (let [c (->> days
               (remove false?)
               count)]
    (str c " nice days" " " "(" (int (* 100 (/ c (count days)))) "%" ")")))
