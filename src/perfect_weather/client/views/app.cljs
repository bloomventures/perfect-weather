(ns perfect-weather.client.views.app
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch subscribe]]
    [perfect-weather.data.months :refer [months months-abbr]]))

(defn calendar-view [ranges]
  [:div.calendar {:style {:width "80vw"}}
   (into 
     [:div.ranges {:style {:display "flex"
                           :margin-bottom "-2.5em"
                           :min-height "3em"}}]
     (if (seq ranges)
       (for [[width text] (->> (concat [{:text nil :range [0 0]}] ranges [{:text nil :range [365 365]}])
                               (partition 2 1)
                               (mapcat (fn [[{[start end] :range text :text} {[next-start _] :range}]]
                                         [[(- end start) text]
                                          [(- next-start end) nil]]))
                               rest
                               butlast)]
         [:div.range {:style {:width (str (* 100 (/ width 365)) "%")
                              :display "flex"
                              :flex-direction "column"
                              :align-items "center"}}
          [:div.text {:style {:text-align "center"
                              :white-space "nowrap"}}
           text]
          [:div.bar {:style {:height "2em"
                             :width "100%"
                             :margin "0.5em 0"
                             :border-radius "5px"
                             :background (when text "green")}}]])
       [[:div.range {:style {:width "100%"
                             :display "flex"
                             :flex-direction "column"
                             :align-items "center"}}
         ":( Never"]]))
   (into 
     [:div.months {:style {:display "flex"}}]
     (for [month months-abbr]
       [:div.month {:style {:width (str (/ 100 12) "%")
                            :border-left "1px solid #ccc"
                            :border-right (when (= month (last months-abbr))
                                            "1px solid #ccc")
                            :text-align "center"
                            :padding-top "2.5em"
                            :z-index -1
                            :box-size "border-box"}} month]))])

(defn result-view [result]
 [:div.result
  [:div.meta
   [:div.location
    [:div.city
     (result :city) ", "]
    [:div.country 
     (result :country)]]
   [:div.stats
    (result :percent) "% 👌"]]
  [calendar-view (result :ranges)]])

(defn results-view []
  [:div.results
   (doall
     (for [result @(subscribe [:results])]
       ^{:key (result :city)}
       [result-view result]))])

(defn form-view []
  [:form
   "When is the weather nice in "
   [:input {:value @(subscribe [:query])
            :on-change (fn [e]
                         (dispatch [:update-query! (.. e -target -value)]))
            :on-focus (fn [_]
                        (dispatch [:update-query! ""]))
            :on-blur (fn [_]
                       (when (string/blank? @(subscribe [:query]))
                         (dispatch [:reset-query!])))}] 
   "?"
   (let [autocomplete-results @(subscribe [:autocomplete-results])]
     (when (seq autocomplete-results)
       [:div.autocomplete-results
        (for [result autocomplete-results]
          ^{:key (result :place-id)}
          [:div.result
           {:on-click (fn []
                        (dispatch [:select-city! result]))}
           (str (result :city) ", " (result :country))])]))])

(defn app-view []
  [:div.app
   [form-view]
   [results-view]])
