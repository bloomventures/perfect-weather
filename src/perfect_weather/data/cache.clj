(ns perfect-weather.data.cache
  (:require
    [clojure.java.io :as io]))

(defn log [& args]
  #_(apply println args))

(defn with-cache 
  [cache-id query-id fetch-fn args]
  (future
    (log "Fetching data for..." (name cache-id) query-id)
    (let [path (str "data/" (name cache-id) "/" query-id ".edn")]
      (if (.exists (io/file path))
        (do
          (log "Returning cached result.")
          (read-string (slurp path)))
        (do
          (log "Fetching from API...")
          (if-let [result @(fetch-fn args)]
            (do
              (log "Successful.")
              (spit path result)
              result)
            (log "Fetch failed.")))))))
