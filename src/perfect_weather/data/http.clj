(ns perfect-weather.data.http
  (:require
    [cheshire.core :as json]
    [org.httpkit.client :as http]))

(defn fetch 
  [{:keys [url params]}]
  (let [response @(http/get url {:throw-exceptions false})]
    (if (= 200 (:status response))
      (-> response
          :body
          (json/parse-string true))
      (println response))))
