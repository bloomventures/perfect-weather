(ns perfect-weather.client.ui.index-page
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [dispatch subscribe]]
    [reagent.core :as r]
    [perfect-weather.client.state.routes :as routes]
    [perfect-weather.data.months :refer [months months-abbr]]
    [perfect-weather.client.ui.autocomplete :refer [autocomplete-view]]))

(def loading-messages
  ["Reticulating splines..."
   "Praying to the weather gods..."
   "Chasing rainbows..."
   "Reading the clouds..."
   "Searching for a hygrometer..."
   "Predicting the weather..."
   "Pulling head out clouds..."
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
       (for [[k start end text] ranges]
         [:div.range.result
          {:class [(when (= start 0) "start")
                   (when (= end 365) "end")
                   (when k (name k))]
           :style {:width (str (* 100 (/ (- end start) 365)) "%")}}
          [:div.bar]
          [:div.label 
           (when k (name k))]])))
   (into 
     [:div.columns.months]
     (for [month months-abbr]
       [:div.column 
        [:div.label month]]))])

(defn result-view [result]
  [:div.result.row
   [:div.legend
    [:div.label
     [:div.city
     (result :city)] 
     [:div.country 
      (result :country)]]]
   [calendar-view result]])

(defn results-view []
  [:div.results
   (doall
     (for [result @(subscribe [:results])]
       ^{:key (result :place-id)}
       [result-view result]))])

(defn form-view []
  [:form {:on-submit (fn [e]
                       (.preventDefault e))}
   "When's the best weather in "
   [autocomplete-view 
    {:auto-focus? true
     :value (subscribe [:query])
     :on-change (fn [value]
                  (dispatch [:update-query! value]))
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

(defn footer-view []
  [:div.footer

   [:div
    [:a {:href (routes/faq-path)}
     "FAQ"]]

   [:div
    "Built by " 
    [:a {:href "https://bloomventures.io/"
         :rel "noopener"
         :target "_blank"}
     "Bloom"]]

   [:div
    "Powered by "
    [:a {:href "https://darksky.net/poweredby/"
         :rel "noopener"
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
