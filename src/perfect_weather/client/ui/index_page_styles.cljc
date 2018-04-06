(ns perfect-weather.client.ui.index-page-styles
  (:require
    [perfect-weather.client.ui.colors :as colors]
    [garden.stylesheet :refer [at-media]]))

(def mobile {:max-width "750px"})

(def row 
  [:&
   {:display "flex"
    :justify-content "flex-start"
    :flex-wrap "wrap"
    :box-sizing "border-box"
    :max-width "100vw"}

   [:>.legend
    {:width "10rem"
     :flex-shrink 0
     :flex-grow 1
     :box-sizing "border-box"}]

   [:>.main
    {:flex-grow 2
     :min-width "30rem"}

    (at-media mobile
      [:&
       {:min-width "100vw"}])

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
    :align-items "center"}

   [:>form
    {:font-size "1.5rem"
     :max-width "100vw"
     :text-align "center"
     :margin "1rem 0 2rem"}

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
       :text-align "left"
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

        [:>.name
         {:white-space "nowrap"}]

        [:>.known]]]]]]

   [:>.results
    {:width "100%"}

    (let [height "4rem"]
      [:>.result
       row

       (at-media mobile
         [:&
          {:margin-bottom "3rem"}])

       [:>.legend
        {:position "relative"
         :background "white"}

        [:>.label
         {:position "absolute"
          :right 0
          :height height
          :line-height height
          :text-transform "uppercase"
          :padding "0 0.25rem"
          :box-sizing "border-box"
          :font-size "0.85rem"
          :letter-spacing "0.1em"
          :text-align "right"
          :white-space "nowrap"
          :font-weight "bold"
          :color colors/accent}
         
         (at-media mobile
           [:&
            {:position "static"
             :height "3rem"
             :line-height "3rem"
             :margin-left "0.5rem"
             :text-align "left"}])]

        [:&:first-letter
         {:font-size "1.15em"}]]

       [:>.calendar

        [:>.columns.background
         {:margin-bottom (str "-" height)
          :height height}]

        [:>.ranges
         {:display "flex"
          :height height
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

          [:&.loop
           {:border-radius "0 !important"}]]]

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
              :text-transform "uppercase"
              :font-size "0.7em"
              :letter-spacing "0.1em"
              :text-align "center"}

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
     :max-width "100vw"
     :padding "0.25rem"}

    [:>div
     {:color colors/text-light
      :margin "0.25rem 0.5rem"
      :white-space "nowrap"}

     [:>a
      {:color colors/accent
       :text-decoration "none"}]]]])
