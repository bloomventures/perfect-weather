(ns perfect-weather.client.state.events
  (:require
    [clojure.string :as string]
    [bloom.omni.fx.ajax :as ajax]
    [bloom.omni.fx.dispatch-debounce :as dispatch-debounce]
    [bloom.omni.fx.router :as router]
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
          :results []
          :map-data []}
     :router [:init!]}))

(reg-event-fx
  :route-page!
  (fn [{db :db} [_ page]]
    {:db (assoc db :page page)
     :dispatch [:-set-page-title!]}))

(reg-event-fx :-set-page-title!
  (fn [{db :db} _]
    (let [title (case (db :page)
                  :faq "Best weather in... (FAQ)"
                  ; default
                  (if-not (string/blank? (db :query))
                    (str "Best weather in " (db :query))
                    "Best weather in..."))]
      {:title title})))

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
                                      :place-id (or (place :place-id) :temp)
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
  :fetch-map-data!
  (fn [{db :db} _]
    (when (empty? (db :map-data))
      {:ajax {:method :get
              :uri "/api/all"
              :on-success (fn [response]
                            (dispatch [:-store-map-data! response]))}})))

(reg-event-fx
  :-store-map-data!
  (fn [{db :db} [_ data]]
    {:db (assoc db :map-data data)}))

(reg-event-fx
  :-fetch-result!
  (fn [_ [_ {:keys [city coutnry] :as place}]]
    {:ajax {:method :get
            :uri "/api/search"
            :params place 
            :on-success (fn [result]
                          (dispatch [:-store-result! result]))
            :on-error (fn [_]
                        (dispatch [:-store-result-error! place]))}}))

(reg-event-fx
  :-store-result!
  (fn [{db :db} [_ result]]
    {:db (-> db
             ; remove the temporary placeholder for the place (or previously searched result)
             (update :results #(vec (remove (fn [r] (or
                                                      (= :temp (r :place-id))
                                                      (= (result :place-id) (r :place-id)))) %)))
             (update :results conj result))}))

(reg-event-fx
  :-store-result-error!
  (fn [{db :db} [_ {:keys [city country] :as place}]]
    {:db (-> db
             ; update the temporary placeholder for the place 
             (update :results (fn [results]
                                (->> results
                                     (map (fn [r]
                                            (if (and 
                                                  (= country (r :country)) 
                                                  (= city (r :city)))
                                              (assoc r :error? true)
                                              r)))))))}))
