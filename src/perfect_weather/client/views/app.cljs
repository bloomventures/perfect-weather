(ns perfect-weather.client.views.app
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch subscribe]]
    [perfect-weather.data.months :refer [months months-abbr]]))

(defn calendar-view [ranges]
  [:div.calendar.main 
   (into
     [:div.columns]
     (repeat 12 [:div.column]))
   (into 
     [:div.ranges]
     (if (seq ranges)
       (for [[start end text] 
             (->> (concat [{:text nil :range [0 0]}] 
                          ranges 
                          [{:text nil :range [365 365]}])
                  (partition 2 1)
                  (mapcat (fn [[{[start end] :range text :text} {[next-start _] :range}]]
                            [[start end text]
                             [end next-start nil]]))
                  rest
                  butlast)]
         [:div.range {:class [(when (= start 0)
                                "start")
                              (when (= end 365)
                                "end")
                              (when text "fill")]
                      :title text
                      :style {:width (str (* 100 (/ (- end start) 365)) "%")}}])
       [[:div.range.never "☹"]]))])

(defn result-view [result]
 [:div.result.row
  [:div.legend
   (result :city)]
  [calendar-view (result :ranges)]])

(defn results-view []
  [:div.results
   (doall
     (for [result @(subscribe [:results])]
       ^{:key (result :city)}
       [result-view result]))
   
   [:div.row.bottom-legend
    [:div.legend]
    [:div.main
     (into 
       [:div.columns]
       (for [month months-abbr]
         [:div.column month]))]]])

(defn form-view []
  [:form
   "When's the weather nice in "
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
