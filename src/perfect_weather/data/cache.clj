(ns perfect-weather.data.cache
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]))

(defn log [& args]
  #_(apply println args))

(defn- sanitize 
  "Sanitize a file name"
  [file-name]
  (string/replace file-name #"[^0-9a-zA-Z\.\-\ ]" ""))

(defn- cache-path 
  [cache-id query-id]
  (str "data/" (name cache-id) "/" (sanitize query-id) ".edn"))

(defn in-cache? 
  [cache-id query-id]
  (.exists (io/file (cache-path cache-id query-id))))

(defn with-cache 
  [cache-id query-id fetch-fn args]
  (future
    (log "Fetching data for..." (name cache-id) query-id)
    (if (in-cache? cache-id query-id) 
      (do
        (log "Returning cached result.")
        (read-string (slurp (cache-path cache-id query-id))))
      (do
        (log "Fetching from API...")
        (if-let [result @(fetch-fn args)]
          (do
            (log "Successful.")
            (spit (cache-path cache-id query-id) result)
            result)
          (log "Fetch failed."))))))


