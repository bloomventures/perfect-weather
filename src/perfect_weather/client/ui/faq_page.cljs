(ns perfect-weather.client.ui.faq-page
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
     [:h2 "What weather is considered 'nice'?"]
     [:p "The weather is 'nice' if:"]
     [:ul
      [:li "the temperature and humidity are within a comfortable range (in a t-shirt or sweater)"]
      [:li "it's not raining"]]
     [:p 
      "The acceptable temperature/humidity range is a synthesis of various human thermal-comfort research sources:"]
     [:img {:src "/images/psychometric-graph.png"}]
     [:p
      "A day is considered 'nice' if at least " rate/hour-threshold " hours between " rate/hour-start "am and " (- rate/hour-end 12) "pm were 'nice', based on data from the last 3 years." ]]

    [:div.item
     [:h2 "How does it all work?"]
     [:p "The process is best explained with a diagram:"]
     [:img {:src "/images/how-it-works.png"}]
     [:p "I have a giant list of potential improvements, but the big ones are:"]
     [:ul
      [:li "indicate why periods are not nice, or kinda-nice, such as: 'perfect', 'nice', 'rainy', 'sweater weather', 'hot', 'humid', 'chilly', 'cold'"]
      [:li "indicate in the output when it is 'reliably' vs. 'unreliably' nice"]]
     [:p "If you have any other ideas or suggestions, hit me up at: " 
      [:a {:href (str "mailto:" email)} email]]]

    [:div.item
     [:h2 "That's all cool and all, but... why?"]
     [:p
      "I'm a digital nomad, which means I work remotely, and change places every few months, preferring places that are inexpensive and have nice weather."]
     [:p
      [:a {:href "https://nomadlist.com/" :target "_blank"} "nomadlist.com"]
      " is fantastic for figuring out where to go next, except their weather filtering is meh (there are only three options: below 16C, 16°C - 25°C, and above 25°C; whereas the range I'm looking for is 19-30°C, with adjustments based on humidity)"]
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
