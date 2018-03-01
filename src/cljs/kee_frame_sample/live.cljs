(ns kee-frame-sample.live
  (:require [re-interval.core :refer [register-interval-handlers]]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]
            [kee-frame.core :refer [reg-controller]]))

;; Registers events `:live/start` and `:live/stop` events.
;; We need to implement `:live/tick` ourselves, it will be called every 1000 ms.
(register-interval-handlers :live nil 1000)

(reg-controller :live
                {:params (fn [{:keys [handler]}]
                           (when (= handler :live) true))
                 :start  [:live/start]
                 :stop   [:live/stop]})

(reg-event-fx :live/tick
              (fn [_ _]
                {:http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/fixtures")
                              :params          {:timeFrame :n1}
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)
                              :on-success      [:live/fixtures-loaded]}}))