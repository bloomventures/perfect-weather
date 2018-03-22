(ns perfect-weather.client.state.events
  (:require
    [bloom.omni.ajax :as ajax]
    [re-frame.core :refer [dispatch reg-fx reg-event-fx]]))

(reg-fx :ajax ajax/fx)

(reg-event-fx 
  :init!
  (fn [_ _]
    {:db {:results []
          :query ""}
     :dispatch [:-fetch-initial-data!]}))

(reg-event-fx
  :update-query!
  (fn [{db :db} [_ query]]
    {:db (assoc db :query query)}))

(reg-event-fx
  :reset-query!
  (fn [{db :db} _]
    {:db (assoc db :query (:city (first (db :results))))}))

(reg-event-fx
  :-fetch-initial-data!
  (fn [_ _]
    {:ajax {:method :get
            :uri "/api/random/3"
            :on-success (fn [results]
                          (doseq [result results]
                            (dispatch [:-store-result! result]))
                          (dispatch [:reset-query!]))}}))

(reg-event-fx
  :-fetch-result!
  (fn [_ _]
    {:ajax {:method :get
            :uri "/api/data"
            :on-success (fn [result]
                          (dispatch [:-store-result! result]))}}))

(reg-event-fx
  :-store-result!
  (fn [{db :db} [_ result]]
    {:db (update db :results conj result)}))
