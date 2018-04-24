(ns perfect-weather.client.ui.map
  (:require
    [clojure.string :as string]
    [re-frame.core :refer [subscribe]]))

(defn merc-lat 
  "From https://wiki.openstreetmap.org/wiki/Mercator"
  [lat]
  (* 180 (/ js/Math.PI) (js/Math.log (js/Math.tan (* (/ js/Math.PI 4) (+ 1 (/ lat 90)))))))

(defn polar->cartesian [cx cy r degrees]
  (let [radians (* (/ js/Math.PI 180) (- degrees 90))]
    {:x (+ cx (* r (js/Math.cos radians)))
     :y (+ cy (* r (js/Math.sin radians)))}))

(defn ->path [coll]
  (->> coll
       (map (fn [i]
           (cond 
             (keyword? i)
             (name i)
             :else
             i)))
       (string/join " ")))

(defn arc-path [{:keys [cx cy r start end stroke]}]
  (let [large-arc? (>= (- end start) 180)
        start' (polar->cartesian cx cy r end)
        end' (polar->cartesian cx cy r start)]
    (->path
      [:M (start' :x) (start' :y)
       ; radx rady x-axis-rotation large-arc-flag sweep-flag x-end y-end
       :A r r 0 (if large-arc? 1 0) 0 (end' :x) (end' :y)]
      )))

(defn circle-graph-view 
  [opts ranges]
  (let [size 13
        stroke-width-nice 3]
    [:svg (merge opts
                 {:width (str size "px")
                  :height (str size "px")})
     (into [:g]
           (for [[k start end text] ranges]
             (let [stroke-width (if (= k :nice) stroke-width-nice 2)
                   stroke-color (if (= k :nice) "#4cafef" "#ccc")
                   start-degrees (* 360 (/ start 365))
                   end-degrees  (* 360 (/ end 365))]
               [:path {:fill "none"
                       :stroke stroke-color
                       :stroke-width stroke-width
                       :d (arc-path {:cx (/ size 2) 
                                     :cy (/ size 2)
                                     :r (- (/ size 2) (/ stroke-width 2))
                                     :start start-degrees
                                     :end end-degrees})}])))]))

(defn map-page-view []
  (let [results @(subscribe [:results])
        width 800
        height 500
        lat-> (fn [lat]
                (+ 50 (* 2.1 (+ 90 (merc-lat lat)))))
        lon-> (fn [lon]
                (+ 15 (* 2.1 (+ 180 lon))))]
    [:div.map 
     [:div {:style {:position "relative"
                    ; https://commons.wikimedia.org/wiki/File:Mercator_Projection.svg
                    :background "url(/images/map.svg)"
                    :background-size "contain"
                    :width 800
                    :height 591}}
      (for [result results]
          ^{:key (result :place-id)}
          [:div {:title (result :city)
                 :style {:position "absolute"
                         :bottom (lat-> (result :lat))
                         :left (lon-> (result :lon))}}
           [circle-graph-view {}
            (:ranges result)]])]]))
