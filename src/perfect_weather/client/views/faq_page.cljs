(ns perfect-weather.client.views.faq-page
  (:require
    [perfect-weather.client.state.routes :as routes]
    [perfect-weather.data.rate :as rate]))

(def email "raf@todo.com")

(defn faq-page-view []
  [:div.faq-page

   [:div.header
    [:a.back {:href (routes/index-path)} "«"]
    [:h1 "FAQ"]]

   [:div.items

    [:div.item
     [:h2 "What's considered a 'nice' day?"]
     [:p
      "A day is considered 'nice' if, in 2017, at least " rate/hour-threshold " hours between " rate/hour-start "am and " (- rate/hour-end 12) "pm were within a comfortable temperature/humidity range, according to human thermal comfort research:"]
     [:img {:src "/images/psychometric-graph.png"}]]

    [:div.item
     [:h2 "How does it all work?"]
     [:p "The process is best explained with a diagram:"]
     [:img {:src "/images/how-it-works.png"}]
     [:p "I have a giant list of potential improvements, but the big ones are:"]
     [:ul
      [:li "take rain into account (skipped in v1 because most historical weather APIs have spotty global precipitation data)"]
      [:li "use the last 3-5 years of data (instead of just 1) and indicate in the output when it is 'reliably' vs. 'unreliably' nice"] 
      [:li "summarize each period under a category, such as: 'perfect', 'nice', 'rainy', 'sweater weather', 'hot', 'humid', 'chilly', 'cold'"]]
     [:p "If you have any other ideas or suggestions, hit me up at: " 
      [:a {:href (str "mailto:" email)} email]]]

    [:div.item
     [:h2 "That's all cool and all, but... why?"]
     [:p
      "I'm a digital nomad, which means I work remotely, and change places every few months, preferring places that are inexpensive and have nice weather."]
     [:p
      [:a {:href "https://nomadlist.com/" :target "_blank"} "nomadlist.com"]
      " is fantastic for figuring out where to go next, except their weather filtering is garbage (there's just three options: below 16C, 16°C - 25°C, and above 25°C; whereas the range I'm looking for is 19-30°C, with adjustments based on humidity)"]
     [:p "Wikipedia's climate sections are nice (" [:a {:href "https://en.wikipedia.org/wiki/Warsaw#Climate" :target "_blank"} "example"] "), but:"]
     [:ul
      [:li "'daily mean', 'average low', etc. are hard to mentally translate to 'is it nice?'"]
      [:li "'months' are too coarse of a resolution (in real life, there can be a big difference between, say, early-May and late-May);"]
      [:li "it's a giant pain to compare multiple places;"]
      [:li "averaging over 30-years gives bad predictions thanks to climate change."]]

   
     [:p "Looking up historical weather in your-favorite-weather-website works, but, is incredibly time-intensive"]
     [:p
      "So, since I plan to do this kind of evaluation a lot (every 3 months for the foreeseable future), and I'm a developer, I figured I could probably just scrape together some data and do the analysis for myself. Which I did, and it worked pretty well. Since it's potentially useful to others, I slapped on a pretty interface and made it public."]]

    [:div.item
     [:h2 "Is there a public API?"]
     [:p "No, but, if there's interest, I could make one. Email me at " [:a {:href (str "mailto:" email)} email] "."]]]])
