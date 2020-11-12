(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [kee-frame.fsm.alpha :as fsm]
            [kee-frame.fsm.http :as http]
            [kee-frame-sample.format :as format]
            [re-frame.core :as f]))

(def table-fsm-id #(keyword (str "table-" %)))
(def fixtures-fsm-id #(keyword (str "fixtures-" %)))


(defn table-request-fsm [id] {:id          (table-fsm-id id)
                              :http-xhrio  (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings"))
                              :max-retries 5
                              :on-success  [::table-received id]})

(defn fixtures-request-fsm [id] {:id          (fixtures-fsm-id id)
                                 :http-xhrio  (util/http-get (str "https://api.football-data.org/v2/matches")
                                                             {:params {:competitions id}})
                                 :max-retries 5
                                 :on-success  [::fixtures-received]})

(f/reg-sub ::failed?
  (fn [_ [_ id]]
    (f/subscribe [::fsm/state id]))
  (fn [state]
    (#{::loading-table-failed
       ::loading-fixtures-failed} state)))

(k/reg-controller ::league
                  {:params (fn [{:keys [data path-params]}]
                             (when (= (:name data) :league)
                               (:id path-params)))
                   :start  (fn [_ id]
                             (table-request-fsm id))})

(f/reg-event-db ::fixtures-received
  (fn [db [_ {:keys [matches]}]]
    (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))))

(f/reg-event-fx ::table-received
  (fn [{db :db} [_ id table]]
    {:db             (-> db
                         (assoc-in [id :table] (-> table :standings first :table))
                         (assoc-in [id :league-name] (-> table :competition :name)))
     ::http/http-fsm (fixtures-request-fsm id)}))