(ns perfect-weather.data.google-maps 
  (:require
    [clojure.set :refer [rename-keys]]
    [cheshire.core :as json]
    [environ.core :refer [env]]
    [org.httpkit.client :as http]))

(def base-url "https://maps.googleapis.com/maps/api/place")

(defn place-details 
  "Reference:
   https://developers.google.com/places/web-service/details"
  [place-id]
  (let [response @(http/request 
                    {:method :get
                     :url (str base-url "/details/json")
                     :query-params
                     {:key (env :google-api-key)
                      :placeid place-id}})
        parse (fn [r]
                {:lat (get-in r [:geometry :location :lat])
                 :lon (get-in r [:geometry :location :lng])
                 :city (->> r
                            :address_components
                            (filter (fn [ac]
                                      (contains? (set (ac :types)) "locality")))
                            first
                            :long_name)
                 :country (->> r
                               :address_components
                               (filter (fn [ac]
                                         (contains? (set (ac :types)) "country")))
                               first
                               :long_name)
                 :offset (get-in r [:utc_offset])})]
    (if (= 200 (:status response))
      (-> response
          :body
          (json/parse-string true)
          :result
          parse)
      (println response))))

(defn autocomplete
  "Reference: 
   https://developers.google.com/places/web-service/autocomplete"
  [query]
  (let [response @(http/request 
                    {:method :get
                     :url (str base-url "/autocomplete/json")
                     :query-params
                     {:key (env :google-api-key)
                      :input query
                      :type "(cities)"}})]
    (if (= 200 (:status response))
      (-> response
          :body
          (json/parse-string true)
          :predictions
          (->> (map (fn [p]
                      {:city (-> p :terms first :value)
                       :country (-> p :terms last :value)
                       :place-id (p :place_id)}))))
      (println response)))) 

