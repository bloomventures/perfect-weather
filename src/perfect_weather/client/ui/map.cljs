(ns perfect-weather.client.ui.map
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe]]
    [garden.core :as garden]
    [perfect-weather.data.months :refer [months months-abbr]]
    [perfect-weather.client.ui.footer :refer [footer-view]]))

(defn merc-lat 
  "From https://wiki.openstreetmap.org/wiki/Mercator"
  [lat]
  (* 180 (/ js/Math.PI) (js/Math.log (js/Math.tan (* (/ js/Math.PI 4) (+ 1 (/ lat 90)))))))

(defn map-view [cities]
  (let [width 800
        height 500
        lat-> (fn [lat]
                (+ -45 (* 2.08 (+ 90 (merc-lat lat)))))
        lon-> (fn [lon]
                (+ 13 (* 2.125 (+ 180 lon))))]
    [:div.map {:style {:position "relative"
                       :width 800
                       :height 363}}
     ; https://commons.wikimedia.org/wiki/File:Mercator_Projection.svg
     [:img {:src "/images/map.svg"
            :style {:position "absolute"
                    :width "100%"
                    :pointer-events "none"
                    :opacity 0.25
                    :top 0
                    :left 0
                    :right 0
                    :bottom 0}}]
     (for [city cities]
       ^{:key (city :id)}
       [:div {:title (city :title)
              :style {:position "absolute"
                      :width "4px"
                      :height "4px"
                      :margin-left "-2px"
                      :margin-top "-2px"
                      :border-radius "50%"
                      :background (if (city :nice?) 
                                    "#4cafef"
                                    "#ddd")
                      :z-index (when (city :nice?)
                                 100)
                      :bottom (lat-> (city :lat))
                      :left (lon-> (city :lon))}}])]))

(defn styles [rules]
  [:style {:dangerously-set-inner-HTML {:__html (garden/css rules)}}])

(defn controls-view [_ _ _]
  (let [svg (r/atom nil)
        drag (r/atom nil)
        val-min 0
        val-max 365

        wrap (fn [x]
               (cond
                 (< x val-min)
                 (+ val-max x)
                 (< val-max x)
                 (- x val-max)
                 :else 
                 x))
        mouse-x (fn [x]
                  (let [ctm (.getScreenCTM @svg)]
                    (/ (- x (.-e ctm)) (.-a ctm))))
        mouse->val (fn [e]
                     (let [x (mouse-x (.-clientX e))
                           svg-dims (.getBoundingClientRect @svg)]
                       (* val-max
                          (/ x (.-width svg-dims)))))
        height 20
        thumb-height height
        thumb-width 7
        track-height 5]
    (fn [start end on-change]
      (let [on-change* (fn [start end]
                         (on-change (js/Math.round start)
                                    (js/Math.round end)))
            start-percent (/ start val-max)
            end-percent (/ end val-max)
            midpoint (if (< start end)
                       (/ (+ start end) 2)
                       (wrap (/ (+ start (+ end val-max)) 2)))]
        [:div.controls
         [:div.months 
          (for [month months-abbr]
            ^{:key month}
            [:div.month 
             month])]
         [:svg {:width "100%"
                :ref (fn [el]
                       (when el
                         (reset! svg el)))
                :on-mouse-move (fn [e]
                                 (case @drag
                                   :start
                                   (on-change* (mouse->val e) end)
                                   :end 
                                   (on-change* start (mouse->val e))
                                   :both
                                   (let [v (mouse->val e)
                                         delta (- midpoint v)]
                                     (on-change* (wrap (- start delta))
                                                 (wrap (- end delta))))
                                   nil
                                   (do)))
                :on-mouse-up (fn [e]
                               (reset! drag nil))}
          [:rect.track 
           {:width "100%"
            :height track-height
            :y (/ (- height track-height) 2)
            :fill "#ccc"}]

          (when (< start-percent end-percent)
            [:rect.mid
             {:width (str (* 100 (- end-percent start-percent)) "%")
              :height track-height
              :x (str (* 100 start-percent) "%")
              :y (/ (- height track-height) 2)
              :fill "#4cafef"
              :style {:cursor "pointer"}
              :on-mouse-down (fn [e]
                               (reset! drag :both))}])

          (when (< end-percent start-percent)
            [:rect.mid
             {:width (str (* 100 end-percent) "%")
              :height track-height
              :x 0
              :y (/ (- height track-height) 2)
              :fill "#4cafef"
              :style {:cursor "pointer"}
              :on-mouse-down (fn [e]
                               (reset! drag :both))}])

          (when (< end-percent start-percent)
            [:rect.mid
             {:width (str (* 100 (- 1 start-percent)) "%")
              :height track-height
              :x (str (* 100 start-percent) "%")
              :y (/ (- height track-height) 2)
              :fill "#4cafef"
              :style {:cursor "pointer"}
              :on-mouse-down (fn [e]
                               (reset! drag :both))}])

          [:circle.start.thumb 
           {:r thumb-width
            :cx (str (* 100 start-percent) "%")
            :cy (/ height 2)
            :fill "#4cafef"
            :style {:cursor "pointer"}
            :on-mouse-down (fn [e]
                             (reset! drag :start))}]
          [:circle.end.thumb
           {:r thumb-width
            :cx (str (* 100 end-percent) "%")
            :cy (/ height 2)
            :fill "#4cafef"
            :style {:cursor "pointer"}
            :on-mouse-down (fn [e]
                             (reset! drag :end))}]]]))))

(defn map-page-view []
  (let [vars (r/atom {:start 31
                      :end 58})]
    (fn []
      (let [start (@vars :start)
            end (@vars :end)
            results (->> @(subscribe [:results])
                         (map (fn [result]
                                {:lat (result :lat)
                                 :lon (result :lon)
                                 :id (result :place-id)
                                 :title (result :city)
                                 :nice? (->> (result :ranges)
                                             (filter (fn [[_ start* end* _]]
                                                       (or (and 
                                                             (< start end)
                                                             (< start end*)
                                                             (< start* end))
                                                           (and 
                                                             (< end start)
                                                             (not
                                                               (and
                                                                 (< end start*)
                                                                 (< end* start)))))))
                                             (every? (fn [[factor _ _ _]]
                                                       (or (= factor :warm) 
                                                           (= factor :cool)))))})))]
        [:div.page.map
         [controls-view (@vars :start) (@vars :end) (fn [start end]
                                                      (reset! vars {:start start
                                                                    :end end}))]
         [map-view results]
         [footer-view]]))))
