(ns perfect-weather.analysis.state.events
  (:require
    [bloom.omni.fx.ajax :as ajax]
    [re-frame.core :refer [dispatch reg-fx reg-event-fx]]))

(reg-fx :ajax ajax/fx)

(reg-event-fx 
  :init!
  (fn [_ _]
    {:db {:data []}
     :dispatch [:-fetch-data!]}))

(reg-event-fx
  :-fetch-data!
  (fn [_ _]
    {:ajax {:method :get
            :uri "/api/analysis/3"
            :on-success (fn [response]
                          (dispatch [:-store-data! response]))}}))

(reg-event-fx
  :-store-data!
  (fn [{db :db} [_ data]]
    {:db (assoc db :data data)}))
