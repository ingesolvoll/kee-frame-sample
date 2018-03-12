(ns kee-frame-sample.view.live
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]))

(defn live-fixtures [fixtures]
  [:div
   (->> fixtures
        (map (fn [[league-name league-fixtures]]
               (when league-name
                 ^{:key (str "live-league-" league-name)}
                 [:div
                  [:h1.live-league-header league-name]
                  [:table.live-table
                   [:tbody
                    (map (fn [{:keys [homeTeamName awayTeamName date result status]}]
                           [:tr {:key (str homeTeamName "-" awayTeamName)}
                            [:td.live-date date]
                            [:td.live-team-name homeTeamName]
                            [:td.live-team-name awayTeamName]
                            (let [{:keys [goalsHomeTeam goalsAwayTeam halfTime]} result]
                              [:td goalsHomeTeam " - " goalsAwayTeam
                               (let [{:keys [goalsHomeTeam goalsAwayTeam]} halfTime]
                                 (str " (" goalsHomeTeam " - " goalsAwayTeam ")"))])
                            [:td (case status
                                   "FINISHED" [ic/action-done]
                                   "IN_PLAY" [ic/action-cached]
                                   "TIMED" [:div])]])
                         league-fixtures)]]]))))])

(defn live []
  (let [fixtures @(subscribe [:live-matches])]
    [:div
     [:div {:style {:text-align :right}}
      [:input {:type      :checkbox
               :on-change #(dispatch [:live/toggle-ongoing (.. % -target -checked)])}]
      "Show only ongoing matches"]
     (cond
       (nil? fixtures) [:div.progress-container [ui/linear-progress]]
       (= [] fixtures) [:div "No matches today"]
       (seq fixtures) [live-fixtures fixtures])]))
