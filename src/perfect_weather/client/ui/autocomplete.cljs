(ns perfect-weather.client.ui.autocomplete
  (:require
    [reagent.core :as r]))

(defn autocomplete-view [_]
  (let [active-result-index (r/atom nil)
        set-active-result! (fn [index]
                             (reset! active-result-index index))
        move-active-result! (fn [delta results]
                              (when (seq results)
                                (reset! active-result-index
                                        (if @active-result-index
                                          (mod (+ delta @active-result-index) (count results))
                                          0))))]
    (r/create-class
      {:display-name "autocomplete" 
       :component-did-update
       (fn [this [_ prev-props]]
         (when (not= (:results prev-props)
                     (:results (r/props this)))
           (set-active-result! 0)))

       :reagent-render
       (fn [{:keys [value results auto-focus? on-change on-clear on-select render-result]}]
         [:div.field
          [:input {:value @value
                   :auto-focus auto-focus?
                   :on-change (fn [e]
                                (on-change (.. e -target -value)))
                   :on-key-down-capture 
                   (fn [e] (case (.-keyCode e)
                             38 ; up arrow
                             (do (move-active-result! -1 results)
                                 (.preventDefault e))
                             40 ; down arrow
                             (do (move-active-result! 1 results)
                                 (.preventDefault e))
                             9 ; tab
                             (on-clear)
                             27 ; esc
                             (on-clear)
                             13 ; enter
                             (when (and @active-result-index (seq results))
                               (on-select (nth results @active-result-index)))
                             nil))
                   :on-focus (fn [e]
                               (.. e -target (setSelectionRange (.. e -target -value -length) (.. e -target -value -length))))
                   :on-blur (fn [_]
                              ; need to have a timeout here, b/c otherwise, the on-blur removes
                              ; the autocomplete before the on-click has a chance to trigger
                              (js/setTimeout (fn []
                                               (on-clear))
                                             200))}] 
          (when (seq results)
            [:div.autocomplete-results 
             (->> results
                  (map-indexed 
                    (fn [index result] 
                      ^{:key (result :place-id)}
                      [:div.result 
                       {:class (when (= index @active-result-index) "active")
                        :on-click (fn []
                                    (on-select result))
                        :on-mouse-over (fn []
                                         (set-active-result! index))}
                       (render-result result)]))
                  doall)])])})))
