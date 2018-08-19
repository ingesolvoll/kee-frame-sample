(ns kee-frame-sample.view.league
  (:require [re-frame.core :refer [subscribe dispatch]]
            [kee-frame.core :as k]
            [cljs-react-material-ui.reagent :as ui]))

(defn fixtures []
  (let [fixtures @(subscribe [:fixtures])]
    (cond
      (nil? fixtures) [:div.progress-container [ui/linear-progress]]
      (= [] fixtures) [:div "No matches"]
      (seq fixtures) [:div
                      [:table.league-table
                       [:thead
                        [:tr
                         [:td "Date"]
                         [:td "Home"]
                         [:td "Away"]
                         [:td "Result"]
                         [:td "Half-time"]]]
                       [:tbody
                        (map (fn [{:keys [homeTeam awayTeam utcDate score]}]
                               [:tr {:key (str (:name homeTeam) "-" (:name awayTeam))}
                                [:td utcDate]
                                [:td (:name homeTeam)]
                                [:td (:name awayTeam)]
                                (let [{:keys [fullTime halfTime]} score]
                                  [:td (:homeTeam fullTime) " - " (:awayTeam fullTime)]
                                  [:td " (" (:homeTeam halfTime) " - " (:awayTeam halfTime) ")"])])
                             fixtures)]]])))

(defn table []
  (let [table @(subscribe [:table])]
    (if (nil? table)
      [:div.progress-container [ui/linear-progress]]
      [:div
       [:table.league-table
        [:thead
         [:tr.league-table-row
          [:td.textright "#"]
          [:td "Team"]
          [:td.textright "M"]
          [:td.textright "W"]
          [:td.textright "D"]
          [:td.textright "L"]
          [:td.textright "Points"]]]
        [:tbody
         (map (fn [{:keys [team points position won draw lost playedGames]}]
                [:tr.league-table-row {:key (:name team)}
                 [:td.textright [:strong position]]
                 [:td (:name team)]
                 [:td.textright playedGames]
                 [:td.textright won]
                 [:td.textright draw]
                 [:td.textright lost]
                 [:td.textright points]])
              table)]]])))


(defn league-dispatch []
  (let [league-name (subscribe [:league-name])]
    [:div
     [:strong {:style {:font-size "25px"}} @league-name]
     [:div {:style {:float :right}}
      [k/switch-route #(-> % :route-params :tab)
       "table" (fn [{{id :id} :route-params}]
                 [:a.nav-link {:href (k/path-for [:league :id id :tab :fixtures])} "Latest results"])
       "fixtures" (fn [{{id :id} :route-params}]
                    [:a.nav-link.active {:href (k/path-for [:league :id id :tab :table])} "Table"])]]
     [k/switch-route #(-> % :route-params :tab)
      "table" [table]
      "fixtures" [fixtures]]]))