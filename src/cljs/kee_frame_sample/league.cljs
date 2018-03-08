(ns kee-frame-sample.league
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [kee-frame.core :refer [reg-controller] :as k]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             (:id route-params)))
                 :start  (fn [_ id]
                           [:league/load id])})

(reg-chain :league/load
           {:db         [[:fixtures nil]
                         [:table nil]]
            :http-xhrio {:method          :get
                         :uri             (str "http://api.football-data.org/v1/competitions/" [::k/params 0] "/leagueTable")
                         :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                         :response-format (ajax/json-response-format {:keywords? true})}}

           {:db [[:table [::k/params 1 :standing]]
                 [:league-caption [::k/params 1 :leagueCaption]]]}

           {:http-xhrio {:method          :get
                         :uri             (str "http://api.football-data.org/v1/competitions/" [::k/params 0] "/fixtures")
                         :params          {:timeFrame :p7}
                         :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                         :response-format (ajax/json-response-format {:keywords? true})}}

           {:db [[:fixtures [::k/params 2 :fixtures]]]})