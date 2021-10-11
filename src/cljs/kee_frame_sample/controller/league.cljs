(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [glimt.core :as http]
            [kee-frame-sample.format :as format]
            [re-frame.core :as f]))

(def table-fsm-id #(keyword (str "table-" %)))
(def fixtures-fsm-id #(keyword (str "fixtures-" %)))


(defn table-request-fsm [id]
  {:id          (table-fsm-id id)
   :http-xhrio  (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings"))
   :max-retries 5
   :retry-delay (fn [retries]
                  (-> (js/Math.pow 2 retries)
                      (* 1000)
                      (min 15000)))
   :on-success  [::table-received id]})

(defn fixtures-request-fsm [id]
  {:id          (fixtures-fsm-id id)
   :http-xhrio  (util/http-get (str "https://api.football-data.org/v2/matches")
                               {:params {:competitions id}})
   :max-retries 5
   :on-success  [::fixtures-received]})

(k/reg-controller ::league
                  {:params (fn [{:keys [data path-params]}]
                             (when (= (:name data) :league)
                               (:id path-params)))
                   :start  (fn [_ id]
                             [::http/start (table-request-fsm id)])})

(f/reg-event-db ::fixtures-received
                (fn [db [_ {:keys [matches]}]]
                  (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))))

(f/reg-event-fx
 ::table-received
 (fn [{db :db} [_ id table]]
   {:db          (-> db
                     (assoc-in [id :table] (-> table :standings first :table))
                     (assoc-in [id :league-name] (-> table :competition :name)))
    ::http/start (fixtures-request-fsm id)}))
