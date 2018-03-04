(ns kee-frame-sample.league
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [kee-frame.core :refer [reg-controller] :as k]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             route-params))
                 :start  (fn [_ {:keys [id tab]}]
                           (case tab
                             "table" [:league/load-table id]
                             "fixtures" [:league/load-fixtures id]))})

(reg-chain :league/load-fixtures
           [:fx {:db         [[:loading true]]
                 :http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/competitions/" [::k/params 0] "/fixtures")
                              :params          {:timeFrame :p7}
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :response-format (ajax/json-response-format)}}]
           [:db [[:fixtures [::k/params 0]]
                 [:loading false]]])

(reg-chain :league/load-table
           [:fx {:http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/competitions/" [::k/params 0] "/leagueTable")
                              ;:params          {:matchday 3}
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :response-format (ajax/json-response-format)}}]
           [:assoc-in [:table]])

(reg-sub :table (fn [db] (some-> db :table)))
(reg-sub :fixtures (fn [db] (some-> db :fixtures)))