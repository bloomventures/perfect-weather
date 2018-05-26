(ns perfect-weather.server.api
  (:require
    [perfect-weather.data.places :as places]
    [perfect-weather.data.core :as data]
    [perfect-weather.data.google-maps :as google-maps]
    [perfect-weather.data.compute :as compute]))

(defn analysis [n]
  (->> #_(places/n-random n)
       #_(places/n-random 3)
       (places/by-city "Toronto")
       (map (fn [city]
              (assoc city
                :data (data/city-day-data city))))))

(def routes
  [[[:get "/api/random/:n"]
    (fn [req]
      {:status 200
       :body (compute/n-random (Integer/parseInt (get-in req [:params :n])))})]

   [[:get "/api/autocomplete"]
    (fn [req]
      {:status 200
       :body (google-maps/autocomplete (get-in req [:params :query]))})]

   [[:get "/api/search"]
    (fn [req]
      {:status 200
       :body (cond 
               (get-in req [:params :place-id]) 
               (compute/by-place-id (get-in req [:params :place-id]))

               (and 
                 (get-in req [:params :city]) 
                 (get-in req [:params :country]))
               (compute/by-autocomplete (str (get-in req [:params :city]) ", " (get-in req [:params :country]))))})]

   [[:get "/api/analysis/:n"]
    (fn [req]
      {:status 200
       :body (analysis (Integer/parseInt (get-in req [:params :n])))})]])

