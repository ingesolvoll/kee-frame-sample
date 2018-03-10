(ns kee-frame-sample.league
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [kee-frame.core :refer [reg-controller]]
            [kee-frame.chain :refer [reg-chain-2]]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]
            [kee-frame-sample.format :as format]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             (:id route-params)))
                 :start  (fn [_ id]
                           [:league/load id])})

(reg-chain-2 :league/load
             (fn [{:keys [db]} [_ id]]
               {:db         (assoc db :fixtures nil
                                      :table nil)
                :http-xhrio {:method          :get
                             :uri             (str "http://api.football-data.org/v1/competitions/" id "/leagueTable")
                             :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                             :response-format (ajax/json-response-format {:keywords? true})}})

             (fn [{:keys [db]} [_ id table]]
               {:db         (assoc db :table (:standing table)
                                      :league-caption (:leagueCaption table))
                :http-xhrio {:method          :get
                             :uri             (str "http://api.football-data.org/v1/competitions/" id "/fixtures")
                             :params          {:timeFrame :p7}
                             :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                             :response-format (ajax/json-response-format {:keywords? true})}})

             (fn [{:keys [db]} [_ _ _ fixtures]]
               {:db (assoc db :fixtures (->> fixtures
                                             :fixtures
                                             (map #(update % :date format/format-date))))}))