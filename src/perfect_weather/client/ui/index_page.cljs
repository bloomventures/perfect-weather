(ns perfect-weather.client.ui.index-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch dispatch-sync subscribe]]
    [reagent.core :as r]
    [perfect-weather.data.months :refer [months months-abbr]]
    [perfect-weather.client.ui.autocomplete :refer [autocomplete-view]]
    [perfect-weather.client.ui.footer :refer [footer-view]]))

(def loading-messages
  ["Reticulating splines..."
   "Praying to the weather gods..."
   "Chasing rainbows..."
   "Reading the clouds..."
   "Searching for a hygrometer..."
   "Predicting the weather..."
   "Pulling head out of clouds..."
   "Moving satellites into position..."
   "Dividing by zero..."
   "Checking the weather channel..."
   "Calling the weather man..."
   "Twiddling thumbs..."
   "Searching for patterns..."
   "Forecasting..."
   "Running the numbers..."
   "Breaking the ice..."])

(defn loading-message-view []
  (let [message (r/atom (rand-nth loading-messages))]
    [:div.message {:ref (fn []
                          (js/setTimeout
                            (fn [] (reset! message (rand-nth loading-messages)))
                            5000))}
     [:div.img [:img {:src "/images/sun.svg"}]]
     @message]))

(defn month-labels-view []
  (into
     [:div.columns.months]
     (for [month months-abbr]
       [:div.column
        [:div.label month]])))

(defn calendar-view
  [{:keys [ranges error?]}]
  [:div.calendar.main
   (into
     [:div.columns.background]
     (repeat 12 [:div.column]))
   (into
     [:div.ranges]
     (cond
       error?
       [[:div.range.error "☹ Uh oh, something went wrong..."]]

       (nil? ranges)
       [[:div.range.loading [loading-message-view]]]

       (seq ranges)
       (for [[[prev-k _ _ _]
              [k start end text]
              [next-k _ _ _]] (->> (concat [(first ranges)] ranges [(last ranges)])
                                                 (partition 3 1))]
         [:div.range.result
          {:class [(when (= start 0) "start")
                   (when (= end 365) "end")
                   (when (and
                           (or (= k :warm)
                               (= k :cool))
                           (or (= next-k :warm)
                               (= next-k :cool)))
                     "join-next")
                   (when (and
                           (or (= k :warm)
                               (= k :cool))
                           (or (= prev-k :warm)
                               (= prev-k :cool)))
                     "join-prev")
                   (when k (name k))
                   (when (< (- end start) 21)
                                "short")]
           :style {:width (str (* 100 (/ (- end start) 365)) "%")}}
          [:div.bar]
          [:div.label
           (when k (name k))]])))
   [month-labels-view]])

(defn result-view [result]
  [:div.result.row
   [:div.legend
    [:div.label
     [:div.city
     (result :city)]
     [:div.country
      (result :country)]]]
   [calendar-view result]])

(defn extra-month-labels-row []
  [:div.row.labels
   [:div.legend]
   [:div.calendar.main
    (into
      [:div.columns.months]
      (for [month months-abbr]
        [:div.column
         [:div.label month]]))]])

(defn results-view []
  (let [results @(subscribe [:results])]
    (when (seq results)
      [:div.results {:class (when (<= 5 (count results)) "many")}
       [extra-month-labels-row]
       (doall
         (for [result results]
           ^{:key (result :place-id)}
           [result-view result]))
       [extra-month-labels-row]])))

(defn form-view []
  [:form {:on-submit (fn [e]
                       (.preventDefault e))}
   "When's the best weather in "
   [autocomplete-view
    {:auto-focus? true
     :value (subscribe [:query])
     :on-change (fn [value]
                  (dispatch-sync [:update-query! value]))
     :results @(subscribe [:autocomplete-results])
     :on-clear (fn []
                 (dispatch [:clear-autocomplete-results!]))
     :on-select (fn [result]
                  (dispatch [:select-city! result]))
     :render-result (fn [result]
                      [:div.place
                       [:div.name
                        (str (result :city) ", " (result :country))]
                       (when (result :known?)
                         [:div.known "⚡"])])}]
   "?"])

(defn index-page-view []
  [:div.index-page
   [:div.gap]
   [form-view]
   [results-view]
   [:div.gap]
   [footer-view]])
