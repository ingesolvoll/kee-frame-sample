(ns kee-frame-sample.leagues
  (:require [kee-frame.core :refer [reg-controller]]
            [re-frame.core :refer [reg-event-fx reg-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :leagues
                {:params (constantly true)
                 :start  [:leagues/load]})

(reg-event-fx :leagues/select
              (fn [_ [_ league-id]]
                {:navigate-to [:league :id league-id :tab :fixtures]}))

(reg-event-fx :leagues/load
              [debug]
              (fn [_ _]
                {:http-xhrio {:method          :get
                              :uri             "http://api.football-data.org/v1/competitions/?season=2017"
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)
                              :on-success      [:leagues/loaded]}}))

(reg-event-db :leagues/loaded
              [debug]
              (fn [db [_ leagues]]
                (assoc db :leagues leagues)))

(reg-sub :leagues (fn [db] (:leagues db)))