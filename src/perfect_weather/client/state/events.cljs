(ns perfect-weather.client.state.events
  (:require
    [clojure.string :as string]
    [bloom.omni.ajax :as ajax]
    [bloom.omni.dispatch-debounce :as dispatch-debounce]
    [bloom.omni.router :as router]
    [bloom.omni.fx.title :as title]
    [re-frame.core :refer [dispatch reg-fx reg-event-fx]]
    [perfect-weather.client.state.routes :as routes]))

(reg-fx :ajax ajax/fx)
(reg-fx :dispatch-debounce dispatch-debounce/fx)
(reg-fx :router router/fx)
(reg-fx :title title/fx)

(reg-event-fx 
  :init!
  (fn [_ _]
    {:db {:query ""
          :autocomplete-results []
          :page nil
          :results []}
     :router [:init!]}))

(reg-event-fx
  :route-page!
  (fn [{db :db} [_ page]]
    {:db (assoc db :page page)
     :dispatch [:-set-page-title!]}))

(reg-event-fx :-set-page-title!
  (fn [{db :db} _]
    (let [site "Sunny Pursuits"
          page (case (db :page)
                 :faq "FAQ"
                 :index (db :query))]
      {:title (str site (when-not (string/blank? page) 
                            (str " - " page)))})))

(reg-event-fx
  :update-query!
  (fn [{db :db} [_ query]]
    {:db (assoc db :query query)
     :dispatch-debounce {:id :query
                         :timeout 200
                         :dispatch [:-fetch-autocomplete! query]}}))

(reg-event-fx
  :select-city!
  (fn [{db :db} [_ place]]
    {:router [:navigate! (routes/result-path place)]
     ; store this, so that when the navigation is resolved
     ; it will use the place-id, avoiding an extra query
     :db (assoc db :selected-place place)}))

(reg-event-fx
  :route-city!
  (fn [{db :db} [_ place] ]
    (let [place (or (db :selected-place) place)]
      {:db (assoc db :query (place :city)) 
       :dispatch-n [[:-store-result! {:city (place :city)
                                      :country (place :country)
                                      :ranges nil}]
                    [:-fetch-result! place]
                    [:clear-autocomplete-results!]]})))

(reg-event-fx
  :-fetch-autocomplete!
  (fn [_ [_ query]]
    {:ajax {:method :get
            :uri "/api/autocomplete"
            :params {:query query}
            :on-success (fn [results]
                         (dispatch [:-store-autocomplete-results! results]))}}))

(reg-event-fx
  :-store-autocomplete-results!
  (fn [{db :db} [_ results]]
    {:db (assoc db :autocomplete-results results)}))

(reg-event-fx
  :clear-autocomplete-results!
  (fn [{db :db} [_ results]]
    {:db (assoc db :autocomplete-results [])}))

(reg-event-fx
  :fetch-random!
  (fn [{db :db} _]
    (when (empty? (db :results))
      {:ajax {:method :get
              :uri "/api/random/3"
              :on-success (fn [results]
                            (doseq [result results]
                              (dispatch [:-store-result! result])))}})))

(reg-event-fx
  :-fetch-result!
  (fn [_ [_ place]]
    {:ajax {:method :get
            :uri "/api/search"
            :params place 
            :on-success (fn [result]
                          (dispatch [:-store-result! result]))}}))

(reg-event-fx
  :-store-result!
  (fn [{db :db} [_ result]]
    {:db (-> db
             (update :results #(vec (remove (fn [r] (and 
                                                      (= (result :city) (r :city))
                                                      (= (result :country) (r :country)))) %)))
             (update :results conj result))}))
