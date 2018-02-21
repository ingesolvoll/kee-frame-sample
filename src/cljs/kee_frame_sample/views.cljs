(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame-sample.subs :as subs]))

(defn table []
  (let [league (re-frame/subscribe [:league])]
    (when @league
      [:div
       [:h1 (get @league "leagueCaption")]
       [:table
        [:thead
         [:tr
          [:td "Team"]
          [:td "Points"]]]
        [:tbody
         (map (fn [{:strs [teamName points]}]
                [:tr {:key teamName}
                 [:td teamName]
                 [:td points]])
              (get @league "standing"))]]])))

(defn main-panel []
  (let [leagues (re-frame/subscribe [:leagues])]
    [:div [:ul
           (map (fn [league]
                  [:li {:key (get league "id")}
                   [:a {:href (str "/league/" (get league "id"))}
                    (get league "caption")]])
                @leagues)]
     [table]]))