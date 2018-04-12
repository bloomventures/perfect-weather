(defproject perfect-weather "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [clj-time "0.14.2"]
                 [re-frame "0.10.5"]
                 [reagent "0.8.0-alpha2"]
                 [mount "0.1.12"]
                 [environ "1.1.0"]
                 [ring-middleware-format "0.7.2"]
                 [ring/ring-defaults "0.3.1"]
                 [io.bloomventures/omni "0.9.1"]]

  :plugins [[lein-environ "1.1.0"]
            [lein-exec "0.3.7"]]

  :main perfect-weather.core

  :aliases {"compile-assets" ["exec" "-ep" "(use 'perfect-weather.compile) (compile!)"]}

  :profiles {:uberjar {:aot :all
                       :prep-tasks ["compile-assets"
                                    "compile"]}})
