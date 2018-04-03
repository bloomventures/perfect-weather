(ns perfect-weather.client.ui.index-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch subscribe]]
    [perfect-weather.client.state.routes :as routes]
    [perfect-weather.data.months :refer [months months-abbr]]
    [perfect-weather.client.ui.autocomplete :refer [autocomplete-view]]))

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
   [:div.label
    (result :city) ", " (result :country)]]
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
  [:form {:on-submit (fn [e]
                       (.preventDefault e))}
   "When's the weather nice in "
   [autocomplete-view 
    {:value (subscribe [:query])
     :on-change (fn [value]
                  (dispatch [:update-query! value]))
     :results (subscribe [:autocomplete-results])
     :on-clear (fn []
                 (dispatch [:clear-autocomplete-results!]))
     :on-select (fn [result]
                  (dispatch [:select-city! result]))
     :render-result (fn [result]
                      (str (result :city) ", " (result :country)))}] 
   "?"])

(defn footer-view []
  [:div.footer

   [:div
    [:a {:href (routes/faq-path)}
     "FAQ"]]

   [:div
    "Built by " 
    [:a {:href "https://bloomventures.io/"
         :target "_blank"}
     "Bloom"]]

   [:div
    "Powered by "
    [:a {:href "https://darksky.net/poweredby/"
         :target "_blank"}
     "DarkSky"] " & "
    [:a {:href "https://developers.google.com/places/"}
     "Google"]]])

(defn index-page-view []
  [:div.index-page
   [:div.gap]
   [form-view]
   [results-view]
   [:div.gap]
   [footer-view]])
