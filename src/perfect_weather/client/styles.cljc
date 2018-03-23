(ns perfect-weather.client.styles
  (:require
    [garden.stylesheet :refer [at-import]]))

(def color-accent "#4cafef")
(def color-grid-background "#f5f5f5")
(def color-grid-border "#ddd")

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
       :border-left [["1px" "solid" color-grid-border]]}

      ["&:nth-child(even)"
       {:background color-grid-background}]

      [:&:last-child
       {:border-right [["1px" "solid" color-grid-border]]}]]]]])

(defn styles []
  [(at-import "https://fonts.googleapis.com/css?family=Montserrat:400,600")
   [:body :h1 :h2 :p
    {:margin 0
     :padding 0}]

   [:#app
    {:font-family "Montserrat"}

    [:>.app
     {:display "flex"
      :min-height "100vh"
      :flex-direction "column"
      :justify-content "center"
      :align-items "center"}

     [:>form
      {:font-size "1.5rem"
       :margin "3rem"}

      [:>input
       {:font-size "1.5rem"
        :width "8em"
        :padding "0.25rem"}]]

     [:>.results
      ; fudge to visually center
      {:margin-right "4rem"
       :width "100%"}

      (let [height "4rem"]
        [:>.result
         row
         {:height height}

         [:>.legend
          {:text-transform "uppercase"
           :height height
           :line-height height
           :padding-right "0.5rem"
           :font-size "0.85rem"
           :letter-spacing "0.1em"
           :text-align "right"
           :font-weight "bold"
           :color color-accent}

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

            [:&.never
             {:color color-accent
              :width "100%"
              :font-weight "bold"
              :text-align "center"}]

            [:&.fill
             {:background color-accent
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
             :letter-spacing "0.1em"}]]]])]]]])
