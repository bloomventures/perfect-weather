(ns perfect-weather.client.state.events
  (:require
    [clojure.string :as string]
    [bloom.omni.ajax :as ajax]
    [bloom.omni.dispatch-debounce :as dispatch-debounce]
    [bloom.omni.router :as router]
    [re-frame.core :refer [dispatch reg-fx reg-event-fx]]
    [perfect-weather.client.state.routes]))

(reg-fx :ajax ajax/fx)
(reg-fx :dispatch-debounce dispatch-debounce/fx)
(reg-fx :router router/fx)

(reg-event-fx 
  :init!
  (fn [_ _]
    {:db {:query ""
          :autocomplete-results []
          :page nil
          :results []}
     :dispatch [:-fetch-initial-data!]
     :router [:init!]}))

(reg-event-fx
  :route-page!
  (fn [{db :db} [_ page]]
    {:db (assoc db :page page)}))

(reg-event-fx
  :update-query!
  (fn [{db :db} [_ query]]
    {:db (assoc db :query query)
     :dispatch-debounce {:id :query
                         :timeout 100
                         :dispatch [:-fetch-autocomplete! query]}}))

(reg-event-fx
  :select-city!
  (fn [{db :db} [_ place]]
    {:db (assoc db :query (-> place 
                              :description
                              (string/split #",")
                              first))
     :dispatch-n [[:-fetch-result! (place :place-id)]
                  [:-clear-autocomplete-results!]]}))

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
  :-clear-autocomplete-results!
  (fn [{db :db} [_ results]]
    {:db (assoc db :autocomplete-results [])}))

(reg-event-fx
  :reset-query!
  (fn [{db :db} _]
    {:db (assoc db :query "")}))

(reg-event-fx
  :-fetch-initial-data!
  (fn [_ _]
    {:ajax {:method :get
            :uri "/api/random/5"
            :on-success (fn [results]
                          (doseq [result results]
                            (dispatch [:-store-result! result])))}}))

(reg-event-fx
  :-fetch-result!
  (fn [_ [_ place-id]]
    {:ajax {:method :get
            :uri "/api/search"
            :params {:place-id place-id}
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
