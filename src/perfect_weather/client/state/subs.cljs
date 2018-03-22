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
    (db :results)))
