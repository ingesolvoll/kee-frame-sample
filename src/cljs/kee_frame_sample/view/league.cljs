(ns kee-frame-sample.view.league
  (:require
   [cljs-react-material-ui.reagent :as ui]
   [glimt.core :as http]
   [kee-frame-sample.controller.league :as c]
   [kee-frame.core :as k]
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as r]))

(defn http-loader-view [fsm-id content]
  (r/with-let [state (subscribe [::http/state fsm-id])]
    (let [[primary-state secondary-state] @state]
      (case primary-state
        nil
        [:div]

        ::http/loading
        [:div.progress-container [ui/linear-progress]]

        ::http/error
        [:div [:h3 "An error occurred"]
         (case secondary-state
           ::http/retrying
           [:div
            "Please wait, trying again"
            [:div.progress-container [ui/linear-progress]]]

           ::http/halted
           [:div
            "Could not load data"
            [:div
             [:button {:on-click #(dispatch [::http/restart fsm-id])}
              "Click to try again"]]])]

        ::http/loaded
        content))))

(defn fixtures [id]
  (let [fixtures @(subscribe [:fixtures id])]
    [http-loader-view (c/fixtures-fsm-id id)
     (cond
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
                              fixtures)]]])]))

(defn table [id]
  (let [table @(subscribe [:table id])]
    [http-loader-view (c/table-fsm-id id)
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
               table)]]])]))

(defn request-error []
  [:div "The football results service only accepts a limited number of requests per minute. Your info will appear within one minute, please wait."])

(defn league-dispatch []
  (let [id          (subscribe [:league-id])
        league-name (subscribe [:league-name @id])]
    [:div
     [:strong {:style {:font-size "25px"}} @league-name]
     [:div {:style {:float :right}}
      [k/case-route #(-> % :path-params :tab)
       "table" (fn [{{id :id} :path-params}]
                 [:a.nav-link {:href (k/path-for [:league {:id id :tab "fixtures"}])} "Latest results"])
       "fixtures" (fn [{{id :id} :path-params}]
                    [:a.nav-link.active {:href (k/path-for [:league {:id id :tab "table"}])} "Table"])]]
     [k/case-route #(-> % :path-params :tab)
      "table" [table @id]
      "fixtures" [fixtures @id]]]))
