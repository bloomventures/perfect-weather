(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.places :as places]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.filters :as filters]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.compute :as compute]))

(def routes
  [[:get "/api/random/:n"]
   (fn [req]
     {:status 200
      :body (->> (places/n-random (Integer/parseInt (get-in req [:params :n])))
                 (map compute/compute))})

   [:get "/api/autocomplete"]
   (fn [req]
     {:status 200
      :body (google-maps/autocomplete (get-in req [:params :query]))})

   [:get "/api/search"]
   (fn [req]
     {:status 200
      :body (cond 
              (get-in req [:params :place-id]) 
              (compute/compute-by-place-id (get-in req [:params :place-id]))
              
              (and 
                (get-in req [:params :city]) 
                (get-in req [:params :country]))
              (->> (google-maps/autocomplete (str (get-in req [:params :city]) ", " (get-in req [:params :country])))
                   first
                   :place-id
                   compute/compute-by-place-id))})
   
   [:get "/api/analysis/:n"]
   (fn [req]
     {:status 200
      :body  (->> (places/n-random (Integer/parseInt (get-in req [:params :n])))
                  (map (fn [city]
                         (assoc city
                           :data (data/city-day-data city)))))})])

