(ns perfect-weather.analysis.ui.app
  (:require
    [re-frame.core :refer [subscribe]]
    [perfect-weather.data.rate :as rate]
    [perfect-weather.data.months :refer [months-abbr]]
    [perfect-weather.data.summary :as summary]
    [perfect-weather.data.filters :as filters]))

(def missing-color "red")
(def accent-color "#4cafef")

(defn graph-view 
  "Expects a sequence of 365 values between 0 and 1"
  [data]
  [:div {:style {:height "20px"
                 :display "flex"}} 
   (->> data
        (map-indexed (fn [i v]
                       ^{:key i}
                       [:div {:style {:width "2px"
                                      :height (str (* v 20) "px")
                                      :background accent-color
                                      :margin-top (str (- 20 (* v 20)) "px")}}])))])

(defn hourly-view [data {:keys [bars? clip?]}]
  (let [h 2
        w 2
        clip (if clip?
               (fn [coll]
                 (->> coll
                      (drop rate/hour-start)
                      (take rate/hour-count)))
               identity)]
    [:div
     (->> data
          (partition 365)
          (map-indexed
            (fn [i year]
              ^{:key i}
              [:div.year {:style {:display "flex"
                                  :position "relative"}}

               (when bars?
                 [:div.bars
                  [:div {:style {:position "absolute"
                                 :top (str (+ 2 (* h rate/hour-start)) "px")
                                 :margin-top "1em"
                                 :z-index 10
                                 :width "100%"
                                 :height "1px"
                                 :background "white"}}]
                  [:div {:style {:position "absolute"
                                 :top (str (+ 2 (* h rate/hour-end)) "px")
                                 :margin-top "1em"
                                 :z-index 10
                                 :width "100%"
                                 :height "1px"
                                 :background "white"}}]])

               (->> year
                    (partition 30)
                    (map-indexed (fn [i month]
                                   ^{:key i}
                                   [:div.month 
                                    [:div (months-abbr i)]
                                    [:div {:style {:display "flex"}}
                                     (for [day month]
                                       ^{:key (-> day first :id)}
                                       [:div.day {:style {:background missing-color}}
                                        (for [row (clip day)]
                                          ^{:key (row :id)}
                                          [:div.hour {:style {:width (str w "px")
                                                              :height (str h "px") 
                                                              :background (row :value)}}])])]])))])))]))

(defn bar-view [data]
  "Expects a sequence of boolean values; handles multiple years (ie. more than 365 values)"
  [:div 
   (->> data
        (partition 365)
        (map-indexed 
          (fn [i year]
            ^{:key i}
            [:div.year {:style {:display "flex"
                                :position "relative"}}
             (->> year
                  (partition 30)
                  (map-indexed 
                    (fn [i month]
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
                                                                               accent-color
                                                                               "white")}}]])))]])))])))])

(defn app-view []
  [:div
   (doall
     (for [place @(subscribe [:data])]
       ^{:key (place :city)}
       [:div
        [:h2 (place :city) ", " (place :country)]

        [:table

         #_[:tbody
            [:tr
             [:td "Temperature"]
             [:td
              [hourly-view 
               (->> (place :data)
                    (map (fn [day]
                           (map (fn [row]
                                  {:id (row :epoch)
                                   :value (str "hsl(204,84%," (/ (* 100 (row :temperature))
                                                                 40) "%)")})
                                day))))
               {:bars? true
                :clip? false}]]]

            [:tr
             [:td "Humidity"]
             [:td
              [hourly-view 
               (->> (place :data)
                    (map (fn [day]
                           (map (fn [row]
                                  {:id (row :epoch)
                                   :value (str "hsl(204,84%," (* 100 (row :humidity)) "%)")})
                                day))))
               {:bars? true
                :clip? false}]]]

            [:tr
             [:td "Dew Point"]
             [:td
              [hourly-view 
               (->> (place :data)
                    (map (fn [day]
                           (map (fn [row]
                                  {:id (row :epoch)
                                   :value (str "hsl(204,84%," (* 100 (/ (row :dew-point) 30)) "%)")})
                                day))))
               {:bars? true
                :clip? false}]]]

            [:tr
             [:td "Precipitation"]
             [:td
              [hourly-view 
               (->> (place :data)
                    (map (fn [day]
                           (map (fn [row]
                                  {:id (row :epoch)
                                   :value (if (row :precipitation?)
                                            accent-color
                                            "black")})
                                day))))
               {:bars? true
                :clip? false}]]]]

         [:tbody
          [:tr
           [:td "Nice Hours"]
           [:td
            [hourly-view 
             (->> (place :data)
                  (map (fn [day]
                         (map (fn [row]
                                {:id (row :epoch)
                                 :value (if (rate/nice? row)
                                          accent-color
                                          "black")})
                              day))))
             {:bars? true
              :clip? false}]]]]

         [:tbody
          [:tr
           [:td "-> Combine Days w/ Median"]
           [:td [graph-view (->> (place :data)
                                 (rate/years->median-factor-days rate/nice?))]]]
          [:tr
           [:td "-> Median Filter"]
           [:td [graph-view (->> (place :data)
                                 (rate/years->median-factor-days rate/nice?)
                                 (filters/median-filter 7))]]]

          [:tr
           [:td "-> Threshold " rate/hour-threshold]
           [:td [bar-view (->> (place :data)
                               (rate/years->median-factor-days rate/nice?)
                               (filters/median-filter 7)
                               (map (partial rate/factor-day? rate/nice?)))]]]

          [:tr
           [:td "-> Filter Streaks"]
           [:td [bar-view (->> (place :data)
                               (rate/years->median-factor-days rate/nice?)
                               (filters/median-filter 7)
                               (map (partial rate/factor-day? rate/nice?))
                               (rate/combined-filter rate/nice?))]]]]

         [:tbody
          (for [[label f] [["hot" rate/hot?]
                           ["cold" rate/cold?]
                           ["humid" rate/humid?]
                           ["dry" rate/dry?]
                           ["rainy" rate/rainy?]]]
            ^{:key label}
            [:tr
             [:td label]
             [:td [bar-view (rate/factor-days f (place :data))]]])]
         #_[:tbody
            [:tr
             [:td "Summary"]
             [:td (summary/text (->> (place :data)
                                     (map (fn [hours]
                                            (rate/day-result? rate/nice? hours)))
                                     (rate/combined-filter rate/nice?)))]]
            [:tr
             [:td "Nice Days"]
             [:td (summary/days-count (->> (place :data)
                                           (map (fn [hours]
                                                  (rate/day-result? rate/nice? hours)))
                                           (rate/combined-filter rate/nice?)))]]]]]))]) 

