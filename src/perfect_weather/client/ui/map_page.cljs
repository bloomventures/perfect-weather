(ns perfect-weather.client.ui.map-page
  (:require
    [clojure.string :as string]
    [garden.core :as garden]
    [reagent.core :as r]
    [re-frame.core :refer [subscribe]]
    [perfect-weather.client.ui.colors :as colors]
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
                                    colors/accent
                                    "#ddd")
                      :z-index (when (city :nice?)
                                 100)
                      :bottom (lat-> (city :lat))
                      :left (lon-> (city :lon))}}])]))

(defn styles [rules]
  [:style {:dangerously-set-inner-HTML {:__html (garden/css rules)}}])

(defn controls-view [date-range]
  (let [slider-el (r/atom nil)
        drag (r/atom nil)
        val-min 0
        val-max 365
        wrap (fn wrap [x]
               (cond
                 (< x val-min)
                 (wrap (+ val-max x))
                 (< val-max x)
                 (wrap (- x val-max))
                 :else
                 x))
        pointer->val (fn [e]
                       (let [slider-dims (.getBoundingClientRect @slider-el)
                             x (- (.-clientX e) (.-x slider-dims))]
                         (* val-max
                            (/ x (.-width slider-dims)))))
        height 20
        thumb-height height
        thumb-width 18
        track-height 8
        midpoint (fn [start end]
                   (if (< start end)
                     (/ (+ start end) 2)
                     (wrap (/ (+ start (+ end val-max)) 2))))
        on-change (fn [start end]
                    (reset! date-range [(js/Math.round start)
                                        (js/Math.round end)]))
        on-pointer-down (fn [k]
                          (fn [e]
                            ; delegate pointer events to slider element
                            ; to avoid glitches when the mid bar elements get removed from DOM mid-drag
                            ; (the bar elements get changed when bar goes "around the edge")
                            (.setPointerCapture @slider-el (.-pointerId e))
                            (reset! drag k)))]
    (fn [date-range]
      (let [[start end] @date-range
            start-percent (/ start val-max)
            end-percent (/ end val-max)]
        [:div.controls
         [:div.months
          (for [month months-abbr]
            ^{:key month}
            [:div.month
             month])]
         [:div.slider
          {:style {:position "relative"
                   :width "100%"
                   :height height}
           :ref (fn [el]
                  (when el
                    (reset! slider-el el)))
           ; delegated pointer events
           :on-pointer-move (fn [e]
                              (case @drag
                                nil
                                (do)
                                :start
                                (let [[start end] @date-range]
                                  (on-change (wrap (pointer->val e)) end))
                                :end
                                (let [[start end] @date-range]
                                  (on-change start (wrap (pointer->val e))))
                                :both
                                (let [[start end] @date-range
                                      v (pointer->val e)
                                      delta (- (midpoint start end) v)]
                                  (on-change (wrap (- start delta))
                                             (wrap (- end delta))))))
           :on-pointer-up (fn [e]
                            (reset! drag nil))
           :on-pointer-cancel (fn [e]
                                (reset! drag nil))}
          [:div.track
           {:style {:position "absolute"
                    :width "100%"
                    :height track-height
                    :top (/ (- height track-height) 2)
                    :background "#ddd"}}]

          (when (< start-percent end-percent)
            [:div.mid
             {:on-pointer-down (on-pointer-down :both)
              :style {:touch-action "none" ; necessary
                      :user-select "none" ; necessary
                      :position "absolute"
                      :width (str (* 100 (- end-percent start-percent)) "%")
                      :height track-height
                      :left (str (* 100 start-percent) "%")
                      :top (/ (- height track-height) 2)
                      :background colors/accent
                      :cursor "pointer"}}])

          (when (< end-percent start-percent)
            [:div.mid
             {:on-pointer-down (on-pointer-down :both)
              :style {:touch-action "none" ; necessary
                      :user-select "none" ; necessary
                      :position "absolute"
                      :width (str (* 100 end-percent) "%")
                      :height track-height
                      :left 0
                      :top (/ (- height track-height) 2)
                      :background colors/accent
                      :cursor "pointer"}}])

          (when (< end-percent start-percent)
            [:div.mid
             {:on-pointer-down (on-pointer-down :both)
              :style {:touch-action "none" ; necessary
                      :user-select "none" ; necessary
                      :position "absolute"
                      :width (str (* 100 (- 1 start-percent)) "%")
                      :height track-height
                      :left (str (* 100 start-percent) "%")
                      :top (/ (- height track-height) 2)
                      :background colors/accent
                      :cursor "pointer"}}])

          [:div.start.thumb
           {:on-pointer-down (on-pointer-down :start)
            :style {:touch-action "none" ; necessary
                    :user-select "none" ; necessary
                    :position "absolute"
                    :width thumb-width
                    :height thumb-width
                    :border-radius "50%"
                    :left (str (* 100 start-percent) "%")
                    :top (/ height 2)
                    :margin-left (- (/ thumb-width 2))
                    :margin-top (- (/ thumb-width 2))
                    :background colors/accent
                    :cursor "pointer"}}]

          [:div.end.thumb
           {:on-pointer-down (on-pointer-down :end)
            :style {:touch-action "none" ; necessary
                    :user-select "none" ; necessary
                    :position "absolute"
                    :width thumb-width
                    :height thumb-width
                    :border-radius "50%"
                    :left (str (* 100 end-percent) "%")
                    :top (/ height 2)
                    :margin-left (- (/ thumb-width 2))
                    :margin-top (- (/ thumb-width 2))
                    :background colors/accent
                    :cursor "pointer"}}]]]))))

(defn map-page-view []
  (let [date-range (r/atom [31 58])]
    (fn []
      (let [[start end] @date-range
            results (->> @(subscribe [:map-data])
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
         [:div.gap]
         [controls-view date-range]
         [map-view results]
         [:div.gap]
         [footer-view]]))))
