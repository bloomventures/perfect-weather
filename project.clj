(defproject perfect-weather "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [clj-time "0.14.2"]
                 [re-frame "0.10.5"]
                 [reagent "0.8.1"]
                 [io.bloomventures/omni "0.14.8"]]

  :plugins [[io.bloomventures/omni "0.14.8"]]

  :main perfect-weather.core

  :omni-config perfect-weather.core/site-config

  :profiles {:uberjar {:aot :all
                       :prep-tasks [["omni" "compile"]
                                    "compile"]}})
