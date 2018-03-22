(ns perfect-weather.client.views.analysis
  (:require
    [re-frame.core :refer [subscribe]]
    [perfect-weather.data.rate :as rate]
    [perfect-weather.data.cities :refer [cities]]
    [perfect-weather.data.months :refer [months]]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.filters :as filters]))

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
                  :top (str (+ 2 (* 5 20)) "px")
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
                            (for [hour (->> day (drop 8) (take 12))]
                              ^{:key (hour :id)}
                              [:div.hour {:style {:width "2px"
                                                  :height "5px"
                                                  :background (hour :value)}}])])]])))])

(defn day-result? [f p? data]
  (let [threshold (if p? 6 6)]
    (<= threshold (->> data
                       (drop 10)
                       (take 10)
                       (map f)
                       (filter true?)
                       count))))

(defn summary-view [f p? data]
  [:div {:style {:display "flex"
                 :position "relative"}}
   (->> data
        (map (fn [hours]
               (day-result? f p? hours)))
        (partition 30)
        (map-indexed (fn [i month]
                       ^{:key i}
                       [:div.month 
                        [:div {:style {:display "flex"}}
                         (->> month
                              (map-indexed (fn [i day]
                                             ^{:key i}
                                             [:div.day 
                                              [:div.hour {:style {:width "2px"
                                                                  :height "5px"
                                                                  :background (if day
                                                                                (if p? "blue" "black")
                                                                                "white")}}]])))]])))])


(defn median-view [f p? data]
  [:div {:style {:display "flex"
                 :position "relative"}}
   (->> data
        (map (fn [hours]
               (day-result? f p? hours)))
        (filters/combined-filter)
        (partition 30)
        (map-indexed (fn [i month]
                       ^{:key i}
                       [:div.month 
                        [:div {:style {:display "flex"}}
                         (->> month
                              (map-indexed (fn [i day]
                                             ^{:key i}
                                             [:div.day 
                                              [:div.hour {:style {:width "2px"
                                                                  :height "5px"
                                                                  :background (if day
                                                                                (if p? "blue" "black")
                                                                                "white")}}]])))]])))])

(defn analysis-view []
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
            [hourly-view (->> @(subscribe [:data (city :key)]) 
                              (map (fn [day]
                                     (map (fn [row]
                                            {:id (row :epoch)
                                             :value 
                                             #_(if (row :precipitation?)
                                                 "red"
                                                 "blue")
                                             (case (rate/issue row)
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
           [:td [summary-view rate/hot? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Hot (filter)"]
           [:td [median-view rate/hot? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Cold"]
           [:td [summary-view rate/cold? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Cold (filter)"]
           [:td [median-view rate/cold? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Humid"]
           [:td [summary-view rate/humid? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Humid (filter)"]
           [:td [median-view rate/humid? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Dry"]
           [:td [summary-view rate/dry? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Dry (filter)"]
           [:td [median-view rate/dry? false @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Nice"]
           [:td [summary-view rate/nice? true @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Nice (filtered)"]
           [:td [median-view rate/nice? true @(subscribe [:data (city :key)])]]]
          [:tr
           [:td "Summary"]
           [:td (summary/text (->> @(subscribe [:data (city :key)])
                                   (map (fn [hours]
                                          (day-result? rate/nice? true hours)))
                                   (filters/combined-filter)))]]
          [:tr
           [:td "Nice Days"]
           [:td (summary/days-count (->> @(subscribe [:data (city :key)])
                                         (map (fn [hours]
                                                (day-result? rate/nice? true hours)))
                                         (filters/combined-filter)))]]]]]))]) 

