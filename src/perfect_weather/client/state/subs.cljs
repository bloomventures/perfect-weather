(ns perfect-weather.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub
  :query
  (fn [db _]
    (db :query)))

(reg-sub
  :results
  (fn [db _]
    (reverse (db :results))))

(reg-sub
  :map-data
  (fn [db _]
    (db :map-data)))

(reg-sub
  :autocomplete-results
  (fn [db _]
    (db :autocomplete-results)))

(reg-sub
  :page
  (fn [db _]
    (db :page)))
