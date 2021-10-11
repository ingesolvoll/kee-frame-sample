(ns kee-frame-sample.view.live
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [kee-frame.fsm.beta :as fsm]
            [kee-frame-sample.controller.live :as live]))

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
        live-fsm-state (subscribe [::fsm/state :live])
        init?          (subscribe [::live/init?])]
    [:div
     [:span
      (case @live-fsm-state
        [::live/running ::live/error] "[disconnected, retrying...]"
        [::live/running ::live/loading] "[updating data...]"
        ::live/initializing "[initializing...]"
        [::live/initializing ::live/error] "[error initializing, retrying...]"
        "[connected]")]
     [:div {:style {:text-align :right}}
      [:input {:type      :checkbox
               :on-change #(dispatch [:live/toggle-ongoing (.. % -target -checked)])}]
      "Show only ongoing matches"]
     (cond
       @init? [:div.progress-container [ui/linear-progress]]
       (= {} fixtures) [:h1 "No matches today"]
       (seq fixtures) [live-fixtures fixtures])]))
