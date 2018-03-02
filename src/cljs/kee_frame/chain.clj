(ns kee-frame.chain
  (:require [re-frame.core :as rf]))

(defn db-event [id handler]
  (rf/console :info "Adding chain step db handler " id)
  (rf/reg-event-db id handler))
(defn fx-event [id handler]
  (rf/console :info "Adding chain step fx handler " id)
  (rf/reg-event-fx id handler))

(defn step-id [counter id]
  (keyword
    (str (namespace id) ":" (name id) "-" counter)))

(defmacro reg-event-chain [id & steps]
  `(loop [[step# & rest#] ~steps
          counter# 0]
     (let [[type# step-data#] step#
           event-id# (step-id counter# ~id)]
       (case type#
         :db (db-event event-id# (fn [db#]))
         :fx (fx-event event-id# (fn [{:keys [db#]}]))
         :error (fx-event (step-id "error" ~id) (fn [{:keys [db#]}]))))
     (when rest#
       (recur rest# (inc counter#)))))