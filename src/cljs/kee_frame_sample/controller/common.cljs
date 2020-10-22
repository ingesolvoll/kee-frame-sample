(ns kee-frame-sample.controller.common
  (:require [kee-frame.core :as k]
            [re-frame.core :as f]))

(f/reg-event-db :toggle-drawer
              (fn [db [_ flag]]
                (assoc db :drawer-open? flag)))

(k/reg-controller :hide-drawer-on-navigate
                ;; Will make the controller restart on every route change
                {:params identity
                 :start  [:toggle-drawer false]})