(ns kee-frame-sample.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [kee-frame.core :as k]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [reagent.core :as r]))

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


(defn team []
  (if-let [{:keys [teamName]} @(subscribe [:team])]
    [:div
     [:h1 teamName]
     ]))

(defn live []
  (let [fixtures @(subscribe [:live-matches])]
    (cond
      (nil? fixtures) [:div.progress-container [ui/linear-progress]]
      (= [] fixtures) [:div "No matches today"]
      (seq fixtures) [:div
                      (->> fixtures
                           (map (fn [[league-name league-fixtures]]
                                  (when league-name
                                    ^{:key (str "live-league-" league-name)}
                                    [:div
                                     [:h1.live-league-header league-name]
                                     [:table.live-table
                                      [:tbody
                                       (map (fn [{:keys [homeTeamName awayTeamName date result]}]
                                              [:tr {:key (str homeTeamName "-" awayTeamName)}
                                               [:td.live-date date]
                                               [:td.live-team-name homeTeamName]
                                               [:td.live-team-name awayTeamName]
                                               (let [{:keys [goalsHomeTeam goalsAwayTeam halfTime]} result]
                                                 [:td goalsHomeTeam " - " goalsAwayTeam
                                                  (let [{:keys [goalsHomeTeam goalsAwayTeam]} halfTime]
                                                    (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))])])
                                            league-fixtures)]]]))))])))

(defn league-dispatch []
  (let [route (subscribe [:kee-frame/route])
        league-caption (subscribe [:league-caption])
        {:keys [id tab]} (:route-params @route)]
    (when (and id tab)
      [:div
       [:strong {:style {:font-size "25px"}} @league-caption]
       [:div {:style {:float :right}}
        (case tab
          "table" [:a.nav-link {:href (k/path-for :league :id id :tab :fixtures)} "Latest results"]
          "fixtures" [:a.nav-link.active {:href (k/path-for :league :id id :tab :table)} "Table"])]
       (case tab
         "table" [table id]
         "fixtures" [fixtures])])))

(defn dispatch-main []
  (case (:handler @(subscribe [:kee-frame/route]))
    :league [league-dispatch]
    :team [team]
    :live [live]
    [:div "Loading..."]))