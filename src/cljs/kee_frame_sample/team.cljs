(ns kee-frame-sample.team
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [kee-frame.core :refer [reg-controller] :as k]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

(reg-controller :team
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :team)
                             (:href route-params)))
                 :start  (fn [_ href] [:team/load href])})

(reg-chain :team/load
           {:http-xhrio {:method          :get
                         :uri             [::k/params 0]
                         :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                         :response-format (ajax/json-response-format)}}
           {:db [[:team [::k/params 1]]]})

(reg-sub :team :team)