(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame.core :refer [dispatch-view reg-view]]
            [bidi.bidi :as bidi]
            [kee-frame.state :as state]))

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

(defn fixtures []
  (if-let [{:strs [fixtures]} @(re-frame/subscribe [:fixtures])]
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
  (if-let [{:strs [leagueCaption standing]} @(re-frame/subscribe [:table])]
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
  (if-let [{:strs [teamName]} @(re-frame/subscribe [:team])]
    [:div
     [:h1 teamName]
     ]))

(defn live []
  [:div "Going live..."])

(reg-view :league
          (fn [{:keys [route-params]}]
            (let [{:keys [id tab]} route-params]
              (when (and id tab)
                [:div
                 [:ul.nav
                  [:li.nav-item
                   [:a.nav-link.active {:href "table"} "Table"]]
                  [:li.nav-item
                   [:a.nav-link {:href (bidi/path-for @state/routes :league :id id :tab :fixtures)} "Latest results"]]]
                 (case tab
                   "table" [table id]
                   "fixtures" [fixtures]
                   [:div "Loading..."])]))))

(reg-view :main
          (fn [{:keys [handler]}]
            (case handler
              :index [:div "Something something"]
              :league [dispatch-view :league]
              :team [team]
              :live [live]
              [:div "Loading..."])))

(defn main-panel []
  [:div
   [league-selector]
   [:a {:href "/live"} "Go live"]
   [dispatch-view :main]])