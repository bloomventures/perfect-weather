(ns perfect-weather.client.ui.index-page-styles
  (:require
    [garden.stylesheet :refer [at-media]]
    [perfect-weather.client.ui.colors :as colors]
    [perfect-weather.client.ui.mixins :as mixins]
    [perfect-weather.client.ui.footer-styles :refer [>footer]]))

(defn months []
  (let [height "2rem"]
    [:&

     [:>.column

      [:>.label
       {:height height
        :line-height [[height "!important"]] 
        :text-align "center"}
       (mixins/tiny-text)

       (at-media {:max-width "400px"}
         [:&
          {:visibility "hidden"
           :font-size "0.000001px"}

          [:&:first-letter
           {:visibility "visible"
            :font-size "0.8rem"}]])]]]))

(def row 
  [:&
   {:display "flex"
    :justify-content "flex-start"
    :flex-wrap "wrap"
    :box-sizing "border-box"}

   [:>.legend
    {:width "11rem"
     :flex-shrink 0
     :box-sizing "border-box"}]

   [:>.main
    {:flex-grow 2
     :min-width "30rem"}

    (mixins/at-mobile 
      {:min-width "100%"})

    [:>.columns
     {:display "flex"
      :width "100%"}

     [:>.column
      {:width (str (float (/ 100 12)) "%")}
      (mixins/alternating-colors)]]]])

(defn >index-page []
  [:>.index-page
   {:display "flex"
    :min-height "100vh"
    :flex-direction "column"
    :justify-content "center"
    :align-items "center"
    :width "100%"}

   [:>form
    {:font-size "1.5rem"
     :color colors/text-normal
     :text-align "center"
     :line-height "1.5em"
     :margin "1rem 0 2rem"}

    [:>.field
     {:display "inline-block"
      :line-height "1em"}

     [:>input
      {:font-size "1.5rem"
       :color colors/text-normal
       :width "18rem"
       :padding "0.25rem"
       :border [["1px" "solid" colors/grid-border]]
       :font-family "inherit"
       :box-shadow "0 2px 5px rgba(0,0,0,0.1)"}

      [:&:focus
       {:border-color colors/accent
        :outline "none"}]]

     [:>.autocomplete-results
      {:position "absolute"
       :background "white"
       :z-index 1000
       :text-align "left"
       :font-size "1rem"
       :box-sizing "border-box"
       :min-width "18rem"
       :border [["1px" "solid" colors/grid-border]]
       :border-top "none"
       :box-shadow "0 2px 5px rgba(0,0,0,0.1)"}

      [:>.result
       {:padding "0.25rem"
        :cursor "pointer"
        :border-top [["1px" "solid" "white"]]
        :border-bottom [["1px" "solid" "white"]]}

       [:&.active
        {:background colors/grid-background
         :border-color colors/grid-border}]

       [:>.place
        {:display "flex"
         :justify-content "space-between"}

        [:>.name
         {:white-space "nowrap"}]

        [:>.known]]]]]]

   [:>.results
    {:width "100%"
     :max-width "50em"}

    [:>.labels
     row

     (mixins/at-mobile
       {:display "none !important"})

     [:>.calendar
      [:>.months
       (months)]]]

    [:>.labels:first-child
     {:display "none"}]

    [:&.many>.labels:first-child
     {:display "flex"}]

    (let [height "4rem"
          bar-height "1rem"]
      [:>.result
       row

       (mixins/at-mobile
         {:margin-bottom "3rem"})

       [:>.legend
        {:background "white"
         :display "flex"
         :flex-direction "column"
         :justify-content "center"
         :height height}

        [:>.label
         {:padding "0 0.25rem"
          :box-sizing "border-box"
          :text-align "right"
          :white-space "nowrap"
          :font-weight "bold"
          :color colors/accent}

         [:>.city
          (mixins/small-text)
          {:height bar-height
           :line-height bar-height}

          [:&:first-letter
           {:font-size "1.15em"}]]

         [:>.country
          (mixins/tiny-text)]

         (mixins/at-mobile
           {:height "2rem"
            :line-height "2rem"
            :margin-left "0.5rem"
            :text-align "left"})]]

       [:>.calendar

        [:>.columns.background
         {:margin-bottom (str "-" height)
          :height height}]

        [:>.ranges
         {:display "flex"
          :height height
          :align-items "center"
          ; due to rounding, sum of ranges may get > 100%  
          :overflow "hidden"}

         [:>.range

          [:&.error
           {:color colors/error
            :width "100%"
            :font-weight "bold"
            :text-align "center"}]

          [:&.loading
           {:color colors/accent
            :width "100%"
            :font-weight "bold"
            :text-align "center"}

           [:>.message
            {:display "flex"
             :align-items "center"
             :justify-content "center"
             :height "1em"}

            [:>.img 
             {:animation [["throb" "1s" "infinite" "ease-in-out"]]}

             [:img
              {:width "1.5em"
               :height "1.5em"
               :margin-right "0.25em"
               :animation [["spin" "8s" "infinite" "linear"]]}]]]]

          [:&.result

           [:>.label
            {:margin-left "0.2em"
             :display "inline-block"
             :pointer-events "none"
             :white-space "nowrap"
             :width "100%"
             :overflow "hidden"
             :text-overflow "clip"}
            (mixins/tiny-text)

            [:&.short
             {:visibility "hidden"}]]

           ["&:nth-child(odd)"

            [:>.bar
             {:background "#999"}]

            [:>.label
             {:color "#999"}]]

           ["&:nth-child(even)"

            [:>.bar
             {:background "#bbb"}]

            [:>.label
             {:color "#bbb"}]]

           [:&:hover 

            [:>.bar
             {:background [[colors/text-normal "!important"]]}]

            [:>.label
             {:visibility "visible"
              :color [[colors/text-normal "!important"]]
              :overflow "visible"}]]

           [:&.warm

            [:>.bar
             {:background colors/accent 
              :width "100%"
              :height bar-height
              :border-radius bar-height}]

            [:>.label 
             {:color colors/accent}]]

           [:&.cool

            [:>.bar
             {:background colors/accent-light
              :width "100%"
              :height bar-height
              :border-radius bar-height}]

            [:>.label 
             {:color colors/accent}]]

           [:&.hot
            :&.humid
            :&.cold
            :&.dry
            :&.rainy

            [:>.bar
             {:height "0.2em" ; bar-height * 0.2
              :margin "0.4em 0" ; bar-height * 0.2 * 2
              :border-radius "0.125em"}]]

           [:&.null
            [:>.label
             {:display "none"}]]

           [:&.start
            :&.join-prev

            [:>.bar
             {:border-top-left-radius 0
              :border-bottom-left-radius 0}]]

           [:&.end
            :&.join-next

            [:>.bar
             {:border-top-right-radius 0
              :border-bottom-right-radius 0}]]]]]

        [:>.columns.months
         {:display "none"}

         (mixins/at-mobile
           {:display "flex"})

         (months)]]])]

   [:>.gap
    {:flex-grow 1}]

   (>footer)])
