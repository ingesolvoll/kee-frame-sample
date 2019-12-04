(ns kee-frame-sample.controller.live
  (:require [re-interval.core :refer [register-interval-handlers]]
            [kee-frame.core :refer [reg-controller reg-chain-named reg-event-fx reg-event-db]]
            [kee-frame-sample.util :as util]))

(register-interval-handlers :live nil 10000)

(reg-controller :live-polling
                {:params (fn [route]
                           (when (-> route :data :name (= :live)) true))
                 :start  [:live/start]
                 :stop   [:live/stop]})

(reg-controller :live-startup
                {:params (constantly true)
                 :start  [:live/load-matches]})

(reg-event-fx :live/tick
              (fn [_ _] {:dispatch [:live/load-matches true]}))

(reg-chain-named

  :live/load-matches
  (fn [_ _] {:http-xhrio (util/http-get "https://api.football-data.org/v2/matches"
                                        {:params {}})})
  :live/fixtures->db
  (fn [{:keys [db]} [_ {:keys [matches]}]]
    {:db (assoc db :live-matches matches)}))

(reg-event-db :live/toggle-ongoing
              (fn [db [flag]]
                (assoc db :ongoing-only? flag)))