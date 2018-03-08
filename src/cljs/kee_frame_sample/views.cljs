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
      (nil? fixtures) [:div "Loading..."]
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
      [:div "Loading..."]
      [:div
       [:table.league-table
        [:thead
         [:tr.league-table-row
          [:td.textright "#"]
          [:td "Team"]
          [:td.textright "W"]
          [:td.textright "D"]
          [:td.textright "L"]
          [:td.textright "Points"]]]
        [:tbody
         (map (fn [{:keys [teamName points position wins draws losses]}]
                [:tr.league-table-row {:key teamName}
                 [:td.textright [:strong position]]
                 [:td teamName]
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
      (nil? fixtures) [:div "Loading live matches..."]
      (= [] fixtures) [:div "No matches today"]
      (seq fixtures) [:div
                      [:table.table
                       [:thead
                        [:tr
                         [:td "Date"]
                         [:td "Home"]
                         [:td "Away"]
                         [:td "Result"]]]
                       [:tbody
                        (map (fn [{:keys [homeTeamName awayTeamName date result]}]
                               [:tr {:key (str homeTeamName "-" awayTeamName)}
                                [:td date]
                                [:td homeTeamName]
                                [:td awayTeamName]
                                (let [{:keys [goalsHomeTeam goalsAwayTeam halfTime]} result]
                                  [:td goalsHomeTeam " - " goalsAwayTeam
                                   (let [{:keys [goalsHomeTeam goalsAwayTeam]} halfTime]
                                     (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))])])
                             fixtures)]]])))

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