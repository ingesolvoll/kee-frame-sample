(ns kee-frame-sample.team
  (:require [kee-frame.core :refer [reg-controller reg-chain]]
            [kee-frame-sample.util :as util]))

(reg-controller :team
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :team)
                             (:href route-params)))
                 :start  (fn [_ href] [:team/load href])})

(reg-chain :team/load

           (fn [_ [_ uri]] {:http-xhrio (util/http-get uri)})

           (fn [{:keys [db]} [_ _ team]]
             {:db (assoc db :team team)}))