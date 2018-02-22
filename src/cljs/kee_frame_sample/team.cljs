(ns kee-frame-sample.team
  (:require [kee-frame.core :refer [reg-controller]]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :team
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :team)
                             (:href route-params)))
                 :start  (fn [_ href] [:team/load href])})

(reg-event-fx :team/load
              [debug]
              (fn [_ [_ href]]
                {:http-xhrio {:method          :get
                              :uri             href
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/transit-response-format)
                              :on-success      [:team/loaded]}}))

(reg-event-db :team/loaded
              [debug]
              (fn [db [_ team]]
                (assoc db :team team)))

(reg-sub :team :team)