(ns perfect-weather.client.state.routes
  (:require
    [bloom.omni.router :as router]
    [re-frame.core :refer [dispatch]]))

(router/defroute index-path "/" []
  (dispatch [:route-page! :index]))

(router/defroute faq-path "/faq" []
  (dispatch [:route-page! :faq]))


