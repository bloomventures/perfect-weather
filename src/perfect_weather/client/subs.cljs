(ns perfect-weather.client.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]
    [perfect-weather.cities :refer [cities]]))

(reg-sub
  :data
  (fn [db [_ city]]
    (get-in db [:data city])))
