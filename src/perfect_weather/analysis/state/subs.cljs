(ns perfect-weather.analysis.state.subs
  (:require
    [re-frame.core :refer [reg-sub]]))

(reg-sub
  :data
  (fn [db [_ city]]
    (get-in db [:data city])))
