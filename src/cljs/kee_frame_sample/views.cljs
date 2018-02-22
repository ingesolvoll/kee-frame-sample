(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame-sample.subs :as subs]))

(defn table []
  (let [league (re-frame/subscribe [:league])]
    (when @league
      [:div
       [:h1 (get @league "leagueCaption")]
       [:table.table
        [:thead
         [:tr
          [:td ""]
          [:td "Team"]
          [:td "W"]
          [:td "D"]
          [:td "L"]
          [:td "Points"]]]
        [:tbody
         (map (fn [{:strs [teamName points position wins draws losses _links crestURI]}]
                [:tr {:key teamName}
                 [:td position]
                 [:td [:a {:href (str "?team=" (get-in _links ["team" "href"]))} teamName]]
                 [:td wins]
                 [:td draws]
                 [:td losses]
                 [:td points]])
              (get @league "standing"))]]])))

(defn main-panel []
  (let [leagues (re-frame/subscribe [:leagues])]
    [:div
     [:select.form-control
      (map (fn [league]
             [:option {:key (get league "id")
                       :value (get league "id")}
              (get league "caption")])
           @leagues)]
     [table]]))