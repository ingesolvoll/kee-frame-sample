(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame.core :refer [dispatch-view reg-view]]))

(defn league-selector []
  [:select.form-control
   {:on-change (fn [e]
                 (re-frame/dispatch [:leagues/select (.. e -target -value)])
                 (set! (.. e -target -value) ""))}
   [:option {:value ""} "(Select league)"]
   (map (fn [{:strs [id caption]}]
          [:option {:key   id
                    :value id}
           caption])
        @(re-frame/subscribe [:leagues]))])

(defn table []
  (when-let [{:strs [leagueCaption standing]} @(re-frame/subscribe [:league])]
    [:div
     [:div.row
      [:div.col-md-8
       [:h1 leagueCaption]]
      [:div.col-md-4
       [league-selector]]]
     [:ul.nav
      [:li.nav-item
       [:a.nav-link.active {:href "table"} "Table"]]
      [:li.nav-item
       [:a.nav-link {:href "fixtures"} "Fixtures"]]]
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
       (map (fn [{:strs [teamName points position wins draws losses _links]}]
              [:tr {:key teamName}
               [:td position]
               [:td [:a {:href (str "?team=" (get-in _links ["team" "href"]))} teamName]]
               [:td wins]
               [:td draws]
               [:td losses]
               [:td points]])
            standing)]]]))

(reg-view :league
          (fn [route]
            [table]))

(defn main-panel []
  [:div
   [dispatch-view :league]])