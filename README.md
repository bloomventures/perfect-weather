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
- create a `config.edn` to set the appropriate env variables:
  ```
  {:omni/http-port 1262
   :darksky-api-key "DARKSKY_KEY"
   :google-api-key "GOOGLE_KEY"
   :cache-path "data"}
  ```
- create the data folder `mkdir data`
- run the repl: `lein repl`
- run either the analysis server `(start! :analysis)` or site server `(start! :site)`
