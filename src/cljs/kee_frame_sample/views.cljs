(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
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
  (if-let [{:strs [fixtures]} @(re-frame/subscribe [:live-matches])]
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
  (let [route (re-frame/subscribe [:kee-frame/route])
        {:keys [id tab]} (:route-params @route)]
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
         [:div "Loading..."])])))

(defn dispatch-main []
  (let [route (re-frame/subscribe [:kee-frame/route])]
    (fn []
      (case (:handler @route)
        :index [:div "Something something"]
        :league league-dispatch
        :team [team]
        :live [live]
        [:div "Loading..."]))))

(defn main-panel []
  [:div
   [league-selector]
   [:a {:href "/live"} "Go live"]
   [dispatch-main]])