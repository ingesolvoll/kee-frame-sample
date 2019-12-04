(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :refer [reg-controller reg-chain-named]]
            [kee-frame-sample.util :as util]
            [kee-frame-sample.format :as format]))

(reg-controller :league
                {:params (fn [{:keys [data path-params]}]
                           (when (= (:name data) :league)
                             (:id path-params)))
                 :start  (fn [_ id]
                           [:league/load id])})

(reg-chain-named

  :league/load
  (fn [{:keys [db]} [id]]
    {:db         (assoc db :fixtures nil
                           :table nil)
     :http-xhrio (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings"))})

  :league/load-fixtures
  (fn [{:keys [db]} [id table]]
    {:db         (assoc db :table (-> table :standings first :table)
                           :league-name (-> table :competition :name))
     :http-xhrio (util/http-get (str "https://api.football-data.org/v2/matches")
                                {:params {:competitions id
                                          :dateFrom "2018-08-18"
                                          :dateTo "2018-08-20"}})})

  :league/save-fixtures
  (fn [{:keys [db]} [_ _ {:keys [matches]}]]
    {:db (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))}))