(ns perfect-weather.client.views.app
  (:require
    [re-frame.core :refer [subscribe]]
    [perfect-weather.cities :refer [cities]]))

(defn hot? [d]
  (< 30 (d :temperature)))

(defn cold? [d]
  (< (d :temperature) 17))

(defn humid? [d]
  (< 0.75 (d :humidity)))

(defn dry? [d]
  (< (d :humidity) 0.25))

(defn perfect? [d]
  (and 
    (< 20 (d :temperature) 26) 
    (< 0.45 (d :humidity) 0.55)))

(defn nice? [d]
  (and 
    (not (hot? d))
    (not (cold? d))
    (not (humid? d))
    (not (dry? d))))

(defn issue [d]
  (cond 
    (hot? d) :hot
    (cold? d) :cold
    (humid? d) :humid
    (dry? d) :dry
    (perfect? d) :perfect
    (nice? d) :nice))

(def months ["Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec"])

(defn hourly-view [data]
  [:div {:style {:display "flex"
                 :position "relative"}}
   [:div {:style {:position "absolute"
                  :top (str (+ 2 (* 5 10)) "px")
                  :margin-top "1em"
                  :z-index 10
                  :width "100%"
                  :height "1px"
                  :background "white"}}]
   [:div {:style {:position "absolute"
                  :top (str (+ 2 (* 5 19)) "px")
                  :margin-top "1em"
                  :z-index 10
                  :width "100%"
                  :height "1px"
                  :background "white"}}]
   (->> data
        (partition 30)
        (map-indexed (fn [i month]
                       ^{:key i}
                       [:div.month {:style {:border-right "0px solid white"}}
                        [:div (months i)]
                        [:div {:style {:display "flex"}}
                         (for [day month]
                           ^{:key (-> day first :id)}
                           [:div.day {:style {:background "pink"}}
                            (for [row day]
                              ^{:key (row :id)}
                              [:div.hour {:style {:width "2px"
                                                  :height "5px"
                                                  :background (row :value)}}])])]])))])

(defn hourly-view-mini [data]
  [:div {:style {:display "flex"
                 :position "relative"}}
   (->> data
        (partition 30)
        (map-indexed (fn [i month]
                       ^{:key i}
                       [:div.month {:style {:border-right "0px solid white"}}
                        [:div (months i)]
                        [:div {:style {:display "flex"}}
                         (for [day month]
                           ^{:key (-> day first :id)}
                           [:div.day {:style {:background "pink"}}
                            (for [hour (->> day (drop 10) (take 9))]
                              ^{:key (hour :id)}
                              [:div.hour {:style {:width "2px"
                                                  :height "5px"
                                                  :background (hour :value)}}])])]])))])

(defn day-result? [f p? data]
  (let [threshold (if p? 6 3)]
    (<= threshold (->> data
                       (drop 10)
                       (take 9)
                       (map f)
                       (filter true?)
                       count))))

(defn summary-view [f p? data]
  [:div {:style {:display "flex"
                 :position "relative"}}
   (->> data
        (partition 30)
        (map-indexed (fn [i month]
                       ^{:key i}
                       [:div.month {:style {:border-right "0px solid white"}}
                        [:div {:style {:display "flex"}}
                         (for [day month]
                           ^{:key (-> day first :epoch)}
                           [:div.day 
                            [:div.hour {:style {:width "2px"
                                                :height "5px"
                                                :background (if (day-result? f p? day)
                                                              (if p? "blue" "black")
                                                              "white")}}]])]])))])

(defn app-view []
  [:div
   (doall
     (for [city cities]
       ^{:key (city :key)}
       [:div
        [:h2 (name (city :key))]

        [:table
         [:tbody
          [:tr
           [:td]
           [:td
            [hourly-view-mini (->> @(subscribe [:data (city :key)]) 
                                   (map (fn [day]
                                          (map (fn [row]
                                                 {:id (row :epoch)
                                                  :value 
                                                  #_(if (row :precipitation?)
                                                      "red"
                                                      "blue")
                                                  (case (issue row)
                                                    ;:hot "#880000"
                                                    ;:cold "#000088"
                                                    ;:humid "#888800"
                                                    ;:dry "#880088"
                                                    ;:rain "red"
                                                    :nice "#09afa3"
                                                    :perfect "#70fffb"
                                                    "black")})
                                               day))))]]]
          [:tr
           [:td "Hot"]
           [:td [summary-view hot? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Cold"]
           [:td [summary-view cold? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Humid"]
           [:td [summary-view humid? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Dry"]
           [:td [summary-view dry? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Nice"]
           [:td [summary-view nice? true @(subscribe [:data (city :key)])]]]]]]))])    
        

