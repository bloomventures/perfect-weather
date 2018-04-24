(ns perfect-weather.client.state.routes
  (:require
    [clojure.string :as string]
    [bloom.omni.fx.router :as router]
    [re-frame.core :refer [dispatch]]))

(router/defroute index-path "/" []
  (dispatch [:route-page! :index])
  (dispatch [:fetch-random!]))

(router/defroute faq-path "/faq" []
  (dispatch [:route-page! :faq]))

(router/defroute map-path "/map" []
  (dispatch [:route-page! :map])
  (dispatch [:fetch-random!]))

(defn place->slug [{:keys [city country]}]
  (-> (str city "--" country)
      (string/replace #" " "-")
      (string/lower-case)))

(defn slug->place [slug]
  (let [[city country] (string/split slug #"--" 2)
        city (->> (string/split city #"-")
                  (map string/capitalize)
                  (string/join " "))
        country (->> (string/split country #"-")
                     (map string/capitalize)
                     (string/join " "))]
    {:city city
     :country country}))

(router/defroute -result-path "/:slug" [slug]
  (dispatch [:route-page! :index])
  (dispatch [:route-city! (slug->place slug)]))

(defn result-path [place]
  (-result-path {:slug (place->slug place)}))


