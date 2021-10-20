(ns kee-frame-sample.controller.league
  (:require
   [kee-frame-sample.format :as format]
   [kee-frame-sample.util :as util]
   [kee-frame.core :as k]
   [kee-frame.fsm.beta :as fsm]
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

(f/reg-event-fx
 ::load
 (fn [_ [_ id]]
   {:fx [[:dispatch [::fsm/http (table-request-fsm id)]]
         [:dispatch [::fsm/http (fixtures-request-fsm id)]]]}))

(k/reg-controller
 ::league
 {:params (fn [{:keys [data path-params]}]
            (when (= (:name data) :league)
              (:id path-params)))
  :start  (fn [_ id]
            [::load id])})

(f/reg-event-db
 ::fixtures-received
 (fn [db [_ {:keys [matches]}]]
   (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))))

(f/reg-event-db
 ::table-received
 (fn [db [_ id table]]
   (-> db
       (assoc-in [id :table] (-> table :standings first :table))
       (assoc-in [id :league-name] (-> table :competition :name)))))
