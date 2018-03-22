(ns perfect-weather.data.cache
  (:require
    [clojure.java.io :as io]))

(defn log [& args]
  #_(apply println args))

(defn with-cache 
  [fetch-fn {:keys [api api-key lat lon ymd]}]
  (future
    (log "Fetching data for..." (name api) lat lon ymd)
    (let [path (str "data/" (name api) "/" lat " " lon " " ymd ".edn")]
      (if (.exists (io/file path))
        (do
          (log "Returning cached result.")
          (read-string (slurp path)))
        (do
          (log "Fetching from API...")
          (if-let [result @(fetch-fn {:api-key api-key
                                      :lat lat
                                      :lon lon
                                      :ymd ymd})]
            (do
              (log "Successful.")
              (spit path result)
              result)
            (log "Fetch failed.")))))))
