(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :refer [reg-controller reg-chain-named]]
            [kee-frame-sample.util :as util]
            [kee-frame-sample.format :as format]
            [kee-frame-sample.fsms :as fsms]))

(reg-controller :league
                {:params (fn [{:keys [data path-params]}]
                           (when (= (:name data) :league)
                             (:id path-params)))
                 :start  (fn [_ id]
                           (fsms/league-fsm id))})

(reg-chain-named

 :league/load-table
 (fn [{:keys [db]} [id]]
   {:db         (assoc db :fixtures nil
                          :table nil)
    :http-xhrio (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings"))})

 :league/table-received
 (fn [{:keys [db]} [id table]]
   {:db (-> db
            (assoc-in [id :table] (-> table :standings first :table))
            (assoc-in [id :league-name] (-> table :competition :name)))}))
(reg-chain-named
 :league/load-fixtures
 (fn [{:keys [db]} [id]]
   {:http-xhrio (util/http-get (str "https://api.football-data.org/v2/matches")
                               {:params {:competitions id}})})

 :league/fixtures-received
 (fn [{:keys [db]} [_ _ {:keys [matches]}]]
   {:db (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))}))