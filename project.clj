(defproject perfect-weather "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [clj-time "0.14.2"]
                 [re-frame "0.10.5"]
                 [reagent "0.8.0-alpha2"]
                 [mount "0.1.12"]
                 [environ "1.1.0"]
                 [ring-middleware-format "0.7.2"]
                 [bloom/omni "0.7.0"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-environ "1.1.0"]]

  :main perfect-weather.core)
