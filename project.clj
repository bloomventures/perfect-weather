(defproject perfect-weather "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [clj-time "0.14.2"]
                 [re-frame "0.10.6"]
                 ; need react 16.4.0 for pointer event support
                 ; once reagent 0.8.2 is released
                 ; won't need the react exclusions and explicit deps
                 [reagent "0.8.1" :exclusions [cljsjs/react
                                               cljsjs/react-dom]]
                 [cljsjs/react "16.4.0-0"]
                 [cljsjs/react-dom "16.4.0-0"]
                 [io.bloomventures/omni "0.18.1"]]

  :plugins [[io.bloomventures/omni "0.18.1"]]

  :main perfect-weather.core

  :omni-config perfect-weather.core/site-config

  :profiles {:uberjar {:aot :all
                       :prep-tasks [["omni" "compile"]
                                    "compile"]}})
