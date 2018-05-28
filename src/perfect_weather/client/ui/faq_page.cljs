(ns perfect-weather.client.ui.faq-page
  (:require
    [perfect-weather.client.state.routes :as routes]
    [perfect-weather.data.rate :as rate]))

(def email "rafal.dittwald@gmail.com")

(defn faq-page-view []
  [:div.faq-page

   [:div.header
    [:a.back {:href (routes/index-path)} "«"]
    [:h1 "FAQ"]]

   [:div.items
    [:div.item
     [:h2 "What is 'cool', what is 'warm'?"]
     [:p "The ranges for each location can fall under one of the following categories:"]
     [:ul
      [:li "cold"]
      [:li "cool"]
      [:li "warm"]
      [:li "hot"]
      [:li "humid"]
      [:li "dry"]
      [:li "rainy"]]
     [:p "'Warm' and 'cool' are 'comfortable' ranges with light clothing. In other conditions, it is uncomfortable to be outside for more than an hour."]
     [:p "Apart from 'rainy', the above categories are determined by the temperature and humidity:"]
     [:img {:src "/images/psychometric-graph.png"}]
     [:p "The above model is based on a combination of human thermal-comfort research and personal experience."]] 

    [:div.item
     [:h2 "You can't predict the weather!"]
     [:p
      "You're right, but what this site is doing is closer to 'summarizing the climate' than 'predicting the weather'."]
     [:p "The specifics of how the data is manipulated to get to the results you see is best explained with a diagram:"]
     [:img {:src "/images/how-it-works.png"}]]

    [:div.item
     [:h2 "This is all cool and all, but... why?"]
     [:p
      "I'm a digital nomad, which means I work remotely, and change places every few months, preferring places that are inexpensive and have nice weather."]
     [:p
      [:a {:href "https://nomadlist.com/" :target "_blank"} "nomadlist.com"]
      " is fantastic for figuring out where to go next, except their weather filtering is " [:em "meh"] " (there are only three options: below 16C, 16°C - 25°C, and above 25°C; whereas the range I'm looking for is 19-30°C, with adjustments based on humidity)"]
     [:p "Wikipedia's climate sections are nice (" [:a {:href "https://en.wikipedia.org/wiki/Warsaw#Climate" :target "_blank"} "example"] "), but:"]
     [:ul
      [:li "'daily mean', 'average low', etc. are hard to mentally translate to 'is it nice?'"]
      [:li "'months' are too coarse of a resolution (in real life, there can be a big difference between, say, early-May and late-May);"]
      [:li "it's a giant pain to compare multiple places;"]
      [:li "averaging over 30-years gives bad predictions thanks to climate change."]]
     [:p "Looking up historical weather in your-favorite-weather-website works, but, it takes so. much. time."]
     [:p
      "So, since I plan to do this kind of evaluation a lot (every 3 months for the foreeseable future), and I'm a developer, I figured I could probably just scrape together some data and do the analysis for myself. Which I did, and it worked pretty well. Since it's potentially useful to others, I slapped on a pretty interface and made it public."]
     [:p "If you have any feedback or suggestions, hit me up at: " 
      [:a {:href (str "mailto:" email)} email]]]

    [:div.item
     [:h2 "Is there a public API?"]
     [:p "No, but, if there's interest, I could make one. Email me at " [:a {:href (str "mailto:" email)} email] "."]]]])
