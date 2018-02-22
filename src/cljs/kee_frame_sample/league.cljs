(ns kee-frame-sample.league
  (:require [kee-frame.core :refer [reg-controller]]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             (:id route-params)))
                 :start  (fn [_ id] [:league/load id])})

(reg-event-fx :league/load
              [debug]
              (fn [_ [_ id]]
                {:http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/competitions/" id "/leagueTable")
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)
                              :on-success      [:league/loaded]}}))

(reg-event-db :league/loaded
              [debug]
              (fn [db [_ league]]
                (assoc db :league league)))

(reg-sub :league :league)