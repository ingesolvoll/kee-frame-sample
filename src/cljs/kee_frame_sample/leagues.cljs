(ns kee-frame-sample.leagues
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [kee-frame.core :refer [reg-controller] :as k]
            [re-frame.core :refer [reg-event-fx reg-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :leagues
                {:params (constantly true)
                 :start  [:leagues/load]})

(reg-event-fx :leagues/select
              (fn [{:keys [db]} [_ league-id]]
                {:db          (dissoc db :fixtures :table)
                 :navigate-to [:league :id league-id :tab :table]}))

(reg-chain :leagues/load
           [:fx {:db         [[:loading true]]
                 :http-xhrio {:method          :get
                              :uri             "http://api.football-data.org/v1/competitions/?season=2017"
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)}}]
           [:db [[:leagues [::k/params 0]]
                 [:loading false]]])

(reg-sub :leagues (fn [db] (:leagues db)))