(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame-sample.subs :as subs]))

(defn main-panel []
  (let [leagues (re-frame/subscribe [:leagues])]
    [:ul
     (map (fn [league]
            [:li {:key (get league "id")}
             [:a {:href (str "/league/" (get league "id"))}
              (get league "caption")]])
          @leagues)]))
