(ns kee-frame-sample.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db debug]]))

(reg-event-db :toggle-drawer
              [debug]
              (fn [db [_ flag]]
                (assoc db :drawer-open? flag)))