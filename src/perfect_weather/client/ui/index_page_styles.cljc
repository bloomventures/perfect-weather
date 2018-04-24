(ns perfect-weather.client.ui.index-page-styles
  (:require
    [perfect-weather.client.ui.colors :as colors]
    [garden.stylesheet :refer [at-media]]))

(def mobile {:max-width "760px"})

(defn tiny-text []
  {:text-transform "uppercase"
   :font-size "0.65rem"
   :line-height "1rem"
   :letter-spacing "0.03rem"})

(defn small-text []
  {:font-size "0.85rem"
   :text-transform "uppercase"
   :letter-spacing "0.1em"})

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

    (at-media mobile
      [:&
       {:min-width "100%"}])

    [:>.columns
     {:display "flex"
      :width "100%"}

     [:>.column
      {:width (str (float (/ 100 12)) "%")
       :border-left [["1px" "solid" colors/grid-border]]}

      ["&:nth-child(even)"
       {:background colors/grid-background}]

      [:&:last-child
       {:border-right [["1px" "solid" colors/grid-border]]}]
      
      (at-media mobile
        [:&:first-child
         {:border-left "none"}]

        [:&:last-child
         {:border-right "none"}])]]]])

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

    (let [height "4rem"
          bar-height "1rem"]
      [:>.result
       row

       (at-media mobile
         [:&
          {:margin-bottom "3rem"}])

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
          (small-text)
          {:height bar-height
           :line-height bar-height}

          [:&:first-letter
           {:font-size "1.15em"}]]

         [:>.country
          (tiny-text)]

         (at-media mobile
           [:&
            {:height "2rem"
             :line-height "2rem"
             :margin-left "0.5rem"
             :text-align "left"}])]]

       [:>.calendar

        [:>.columns.background
         {:margin-bottom (str "-" height)
          :height height}]

        [:>.ranges
         {:display "flex"
          :height height
          :align-items "center"}

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
             :color colors/text-light
             :display "inline-block"
             :pointer-events "none"
             :white-space "nowrap"
             :width "100%"
             :overflow "hidden"
             :text-overflow "clip"}
            (tiny-text)]

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
             {:color [[colors/text-normal "!important"]]
              :overflow "visible"}]]

           [:&.nice

            [:>.bar
             {:background colors/accent 
              :width "100%"
              :height bar-height
              :border-radius bar-height}]

            [:>.label 
             {:color colors/accent}]]

           [:&.hot
            :&.hot-and-humid
            :&.humid
            :&.cold
            :&.dry

            [:>.bar
             {:height "0.2em" ; bar-height * 0.2
              :margin "0.4em 0" ; bar-height * 0.2 * 2
              :border-radius "0.125em"}]]

           [:&.null
            [:>.label
             {:display "none"}]]

           [:&.start

            [:>.bar
             {:border-top-left-radius 0
              :border-bottom-left-radius 0}]]

           [:&.end

            [:>.bar
             {:border-top-right-radius 0
              :border-bottom-right-radius 0}]]]]]

        (let [height "2rem"]
          [:>.columns.months
           {:display "none"}

           (at-media mobile
             [:&
              {:display "flex"}])

           [:>.column

            [:>.label
             {:height height
              :line-height height
              :text-align "center"}
             (tiny-text)

             (at-media {:max-width "400px"}
               [:&
                {:visibility "hidden"
                 :font-size "0.000001px"}

                [:&:first-letter
                 {:visibility "visible"
                  :font-size "0.8rem"}]])]]])]])

    [:>.result:last-child>.calendar>.months
     {:display "flex"}]]

   [:>.gap
    {:flex-grow 1}]

   [:>.footer
    {:display "flex"
     :justify-content "center"
     :flex-wrap "wrap"
     :box-sizing "border-box"
     :width "100%"
     :padding "0.25rem"}

    [:>div
     {:color colors/text-light
      :margin "0.25rem 0.5rem"
      :white-space "nowrap"}

     [:>a
      {:color colors/accent
       :text-decoration "none"}]]]])
