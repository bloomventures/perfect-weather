# Perfect Weather

Now live as https://bestweather.in/

## Stack

  - [clojure](https://clojure.org/)
  - [clojurescript](https://clojurescript.org/)
  - [reagent](https://github.com/reagent-project/reagent)
  - [re-frame](https://github.com/Day8/re-frame)
  - [garden](https://github.com/noprompt/garden/)

## Running

- clone
- create a `profiles.clj` to set the appropriate env variables:
  ```
  {:me {:env {:darksky-api-key "DARKSKY_KEY"
              :google-api-key "GOOGLE_KEY"
              :http-port 1262
              :cache-path "data"}}}
  ```
- create the data folder `mkdir data`
- run the repl: `lein with-profiles +me repl`
- run either the analysis server `(start! :analysis)` or client dev server `(start! :dev)`
