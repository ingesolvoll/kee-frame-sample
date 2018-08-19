(ns kee-frame-sample.view.live
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]))

(defn live-fixtures [fixtures]
  [:div
   (->> fixtures
        (map (fn [[competition league-fixtures]]
               (when competition
                 ^{:key (str "live-league-" (:id competition))}
                 [:div
                  [:h1.live-league-header (:name competition)]
                  [:table.live-table
                   [:tbody
                    (map (fn [{:keys [homeTeam awayTeam utcDate score status]}]
                           [:tr {:key (str (:name homeTeam) "-" (:name awayTeam))}
                            [:td.live-date utcDate]
                            [:td (:name homeTeam)]
                            [:td (:name awayTeam)]
                            (let [{:keys [fullTime halfTime]} score]
                              [:td (:homeTeam fullTime) " - " (:awayTeam fullTime)]
                              [:td " (" (:homeTeam halfTime) " - " (:awayTeam halfTime) ")"])
                            [:td (case status
                                   "FINISHED" [ic/action-done]
                                   "IN_PLAY" [ic/action-cached]
                                   [:div])]])
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
       (= {} fixtures) [:h1 "No matches today"]
       (seq fixtures) [live-fixtures fixtures])]))
