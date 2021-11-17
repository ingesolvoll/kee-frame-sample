(ns kee-frame-sample.view.live
  (:require
   [cljs-react-material-ui.icons :as ic]
   [cljs-react-material-ui.reagent :as ui]
   [glimt.core :as http]
   [kee-frame-sample.controller.live :as live]
   [re-frame.core :refer [subscribe dispatch]]
   [re-statecharts.core :as rs]))

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
                           (let [{:keys [fullTime halfTime]} score]
                             [:tr {:key (str (:name homeTeam) "-" (:name awayTeam))}
                              [:td.live-date utcDate]
                              [:td (:name homeTeam)]
                              [:td (:name awayTeam)]
                              [:td (:homeTeam fullTime) " - " (:awayTeam fullTime)]
                              [:td " (" (:homeTeam halfTime) " - " (:awayTeam halfTime) ")"]
                              [:td (case status
                                     "FINISHED" [ic/action-done]
                                     "IN_PLAY" [ic/action-cached]
                                     [:div])]]))
                         league-fixtures)]]]))))])

(defn live []
  (let [fixtures       @(subscribe [:live-matches])
        live-fsm-state (subscribe [::rs/state :live])
        init?          (subscribe [::live/init?])]
    [:div
     [:span
      (rs/match-state @live-fsm-state
                      [::live/running ::live/loading ::http/error ::http/halted] "[could not connect, try refreshing the page]"
                      [::live/running ::live/loading ::http/error ::http/retrying] "[Disconnected, reconnecting]"
                      [::live/running] "[connected]"
                      "[unknown]")]
     [:div {:style {:text-align :right}}
      [:input {:type      :checkbox
               :on-change #(dispatch [:live/toggle-ongoing (.. % -target -checked)])}]
      "Show only ongoing matches"]
     (cond
       @init? [:div.progress-container [ui/linear-progress]]
       (= {} fixtures) [:h1 "No matches today"]
       (seq fixtures) [live-fixtures fixtures])]))
