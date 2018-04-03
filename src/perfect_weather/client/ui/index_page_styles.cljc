(ns perfect-weather.client.ui.index-page-styles
  (:require
    [perfect-weather.client.ui.colors :as colors]))

(def row 
  [:&
   {:display "flex"
    :justify-content "center"}

   [:>.legend
    {:width "10rem"
     :flex-shrink 0
     :box-sizing "border-box"}]

   [:>.main
    {:width "100%"
     :flex-grow 2
     :min-width "25rem"
     :max-width "50rem"}

    [:>.columns
     {:display "flex"
      :height "100%"
      :width "100%"}

     [:>.column
      {:width (str (float (/ 100 12)) "%")
       :border-left [["1px" "solid" colors/grid-border]]}

      ["&:nth-child(even)"
       {:background colors/grid-background}]

      [:&:last-child
       {:border-right [["1px" "solid" colors/grid-border]]}]]]]])

(defn >index-page []
  [:>.index-page
   {:display "flex"
    :min-height "100vh"
    :flex-direction "column"
    :justify-content "center"
    :align-items "center"}

   [:>form
    {:font-size "1.5rem"
     :margin "3rem"}

    [:>.field
     {:display "inline-block"}

     [:>input
      {:font-size "1.5rem"
       :width "12rem"
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
       :font-size "1rem"
       :box-sizing "border-box"
       :width "12rem"
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

        [:>.name]

        [:>.known]]]]]]

   [:>.results
    ; fudge to visually center
    {:margin-right "4rem"
     :width "100%"}

    (let [height "4rem"]
      [:>.result
       row
       {:height height}

       [:>.legend
        {:position "relative"}

        [:>.label
         {:position "absolute"
          :right 0
          :text-transform "uppercase"
          :height height
          :line-height height
          :padding-right "0.5rem"
          :font-size "0.85rem"
          :letter-spacing "0.1em"
          :text-align "right"
          :white-space "nowrap"
          :font-weight "bold"
          :color colors/accent}]

        [:&:first-letter
         {:font-size "1.15em"}]]

       [:>.calendar

        [:>.columns
         {:margin-bottom (str "-" height)} 

         [:>.month]]

        [:>.ranges
         {:display "flex"
          :height "100%" 
          :align-items "center"}

         [:>.range
          {:height "1em"}

          [:&.loading
           {:color colors/accent
            :width "100%"
            :font-weight "bold"
            :text-align "center"}]

          [:&.never
           {:color colors/accent
            :width "100%"
            :font-weight "bold"
            :text-align "center"}]

          [:&.fill
           {:background colors/accent
            :border-radius "1em"}]

          [:&.start
           {:border-radius "0 1em 1em 0"}]

          [:&.end
           {:border-radius "1em 0 0 1em"}]]]]])

    (let [height "2rem"]
      [:>.bottom-legend
       row
       {:height height}

       [:>.main

        [:>.columns

         [:>.column
          {:line-height height
           :text-align "center"
           :text-transform "uppercase"
           :font-size "0.7em"
           :letter-spacing "0.1em"}]]]])]

   [:>.gap
    {:flex-grow 1}]

   [:>.footer
    {:display "flex"
     :padding "0.25rem"}

    [:>div
     {:margin "0 0.5rem"
      :color colors/text-light}

     [:>a
      {:color colors/accent
       :text-decoration "none"}]]]])
