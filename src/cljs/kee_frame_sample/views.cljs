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

(defn table []
  (let [table @(subscribe [:table])]
    (if (nil? table)
      [:div "Loading..."]
      [:div
       [ui/table {}
        [ui/table-header {:display-select-all false}
         [ui/table-header-column {:width 10} ""]
         [ui/table-header-column {:width 150} "Team"]
         [ui/table-header-column "W"]
         [ui/table-header-column "D"]
         [ui/table-header-column "L"]
         [ui/table-header-column "Points"]]
        [ui/table-body {:display-row-checkbox false}
         (map (fn [{:keys [teamName points position wins draws losses]}]
                [ui/table-row {:key teamName}
                 [ui/table-row-column {:width 10} position]
                 [ui/table-row-column {:width 150} teamName]
                 [ui/table-row-column wins]
                 [ui/table-row-column draws]
                 [ui/table-row-column losses]
                 [ui/table-row-column points]])
              table)]]])))


(defn team []
  (if-let [{:strs [teamName]} @(subscribe [:team])]
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
       [:h1 @league-caption]
       (case tab
         "table" [:a.nav-link {:href (k/path-for :league :id id :tab :fixtures)} "View latest results"]
         "fixtures" [:a.nav-link.active {:href (k/path-for :league :id id :tab :table)} "View table"])
       (case tab
         "table" [table id]
         "fixtures" [fixtures])])))

(defn dispatch-main []
  (case (:handler @(subscribe [:kee-frame/route]))
    :league [league-dispatch]
    :team [team]
    :live [live]
    [:div "Loading..."]))