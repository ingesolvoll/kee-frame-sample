(ns kee-frame-sample.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [kee-frame.core :as k]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [reagent.core :as r]))

(defn fixtures []
  (if-let [{:strs [fixtures]} @(subscribe [:fixtures])]
    [:div
     [:table.table
      [:thead
       [:tr
        [:td "Date"]
        [:td "Home"]
        [:td "Away"]
        [:td "Result"]]]
      [:tbody
       (map (fn [{:strs [homeTeamName awayTeamName date result]}]
              [:tr {:key (str homeTeamName "-" awayTeamName)}
               [:td date]
               [:td homeTeamName]
               [:td awayTeamName]
               (let [{:strs [goalsHomeTeam goalsAwayTeam halfTime]} result]
                 [:td goalsHomeTeam " - " goalsAwayTeam
                  (let [{:strs [goalsHomeTeam goalsAwayTeam]} halfTime]
                    (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))])])
            fixtures)]]]))

(defn table []
  (if-let [{:strs [leagueCaption standing]} @(subscribe [:table])]
    [:div
     [:h1 leagueCaption]
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
       (map (fn [{:strs [teamName points position wins draws losses]}]
              [:tr {:key teamName}
               [:td position]
               [:td teamName]
               [:td wins]
               [:td draws]
               [:td losses]
               [:td points]])
            standing)]]]))


(defn team []
  (if-let [{:strs [teamName]} @(subscribe [:team])]
    [:div
     [:h1 teamName]
     ]))

(defn live []
  (if-let [{:strs [fixtures]} @(subscribe [:live-matches])]
    [:div
     [:table.table
      [:thead
       [:tr
        [:td "Date"]
        [:td "Home"]
        [:td "Away"]
        [:td "Result"]]]
      [:tbody
       (map (fn [{:strs [homeTeamName awayTeamName date result]}]
              [:tr {:key (str homeTeamName "-" awayTeamName)}
               [:td date]
               [:td homeTeamName]
               [:td awayTeamName]
               (let [{:strs [goalsHomeTeam goalsAwayTeam halfTime]} result]
                 [:td goalsHomeTeam " - " goalsAwayTeam
                  (let [{:strs [goalsHomeTeam goalsAwayTeam]} halfTime]
                    (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))])])
            fixtures)]]]))

(defn league-dispatch []
  (let [route (subscribe [:kee-frame/route])
        {:keys [id tab]} (:route-params @route)]
    (when (and id tab)
      [:div
       (case tab
         "table" [:a.nav-link {:href (k/path-for :league :id id :tab :fixtures)} "View latest results"]
         "fixtures" [:a.nav-link.active {:href (k/path-for :league :id id :tab :table)} "View table"]
         [:div "..."])
       (case tab
         "table" [table id]
         "fixtures" [fixtures]
         [:div "Loading..."])])))

(defn dispatch-main []
  (case (:handler @(subscribe [:kee-frame/route]))
    :league league-dispatch
    :team [team]
    :live [live]
    [:div "Loading..."]))