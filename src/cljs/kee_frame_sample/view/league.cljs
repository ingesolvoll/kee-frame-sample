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
                        (map (fn [{:keys [homeTeamName awayTeamName date result]}]
                               [:tr {:key (str homeTeamName "-" awayTeamName)}
                                [:td date]
                                [:td homeTeamName]
                                [:td awayTeamName]
                                (let [{:keys [goalsHomeTeam goalsAwayTeam halfTime]} result]
                                  [:td goalsHomeTeam " - " goalsAwayTeam])
                                [:td (let [{:keys [goalsHomeTeam goalsAwayTeam]} (:halfTime result)]
                                       (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))]])
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
         (map (fn [{:keys [teamName points position wins draws losses playedGames]}]
                [:tr.league-table-row {:key teamName}
                 [:td.textright [:strong position]]
                 [:td teamName]
                 [:td.textright playedGames]
                 [:td.textright wins]
                 [:td.textright draws]
                 [:td.textright losses]
                 [:td.textright points]])
              table)]]])))


(defn league-dispatch []
  (let [league-caption (subscribe [:league-caption])]
    [:div
     [:strong {:style {:font-size "25px"}} @league-caption]
     [:div {:style {:float :right}}
      [k/switch-route #(-> % :route-params :tab)
       "table" (fn [{{id :id} :route-params}]
                 [:a.nav-link {:href (k/path-for :league :id id :tab :fixtures)} "Latest results"])
       "fixtures" (fn [{{id :id} :route-params}]
                    [:a.nav-link.active {:href (k/path-for :league :id id :tab :table)} "Table"])]]
     [k/switch-route #(-> % :route-params :tab)
      "table" [table]
      "fixtures" [fixtures]]]))