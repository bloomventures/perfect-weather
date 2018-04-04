(ns perfect-weather.data.google-maps 
  (:require
    [clojure.string :as string]
    [clojure.set :refer [rename-keys]]
    [cheshire.core :as json]
    [environ.core :refer [env]]
    [org.httpkit.client :as http]
    [perfect-weather.data.cache :refer [with-cache in-cache?]]))

(defn round [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

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
                {:place-id place-id
                 :lat (round 2 (get-in r [:geometry :location :lat]))
                 :lon (round 2 (get-in r [:geometry :location :lng]))
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
    (future
      (if (= 200 (:status response))
        (-> response
            :body
            (json/parse-string true)
            :result
            parse)
        (println response)))))

(defn autocomplete-raw
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
    (future (if (= 200 (:status response))
              (-> response
                  :body
                  (json/parse-string true)
                  :predictions
                  (->> (map (fn [p]
                              {:city (-> p :terms first :value)
                               :country (-> p :terms last :value)
                               :place-id (p :place_id)}))
                       ; get rid of dupes
                       (reduce (fn [memo p]
                                 (if (memo [(p :city) (p :country)])
                                   memo
                                   (assoc memo [(p :city) (p :country)] p))) {})
                       vals))
              (println response))))) 

(defn autocomplete
  [query]
  (let [query (string/lower-case query)]
    (->> (with-cache 
           :autocomplete
           query
           autocomplete-raw
           query)
         deref
         (map (fn [place]
                (if (in-cache? :places (place :place-id))
                  (assoc place :known? true)
                  place))))))

(defn place [place-id]
  (->> (with-cache 
         :places
         place-id
         place-details
         place-id)))
