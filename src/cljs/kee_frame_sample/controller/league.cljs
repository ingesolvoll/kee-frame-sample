(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :as k :refer [reg-controller reg-chain-named]]
            [kee-frame-sample.util :as util]
            [kee-frame.fsm :as fsm]
            [kee-frame-sample.format :as format]
            [re-frame.core :as f]))

(defn league-fsm [id]
  {:id    [:league-fsm id]
   :start ::loading-table
   :stop  ::loaded
   :fsm   {::loading-table           {[::fsm/on-enter]            {:dispatch [[:league/load-table id]]}
                                      [:league/table-received id] {:to ::loading-fixtures}
                                      [:default-on-failure]       {:to ::loading-table-failed}}
           ::loading-fixtures        {[::fsm/on-enter]               {:dispatch [[:league/load-fixtures id]]}
                                      [:league/fixtures-received id] {:to ::loaded}
                                      [:default-on-failure]          {:to ::loading-fixtures-failed}}
           ::loading-table-failed    {[::fsm/after 10000] {:to ::loading-table}}
           ::loading-fixtures-failed {[::fsm/after 10000] {:to ::loading-fixtures}}}})

(f/reg-sub ::state
           (fn [[_ id]]
             (f/subscribe [::fsm/state (league-fsm id)]))
           identity)

(f/reg-sub ::failed?
           (fn [_ [_ id]]
             (f/subscribe [::state id]))
           (fn [state]
             (#{::loading-table-failed
                ::loading-fixtures-failed} state)))

(reg-controller :league
                {:params (fn [{:keys [data path-params]}]
                           (when (= (:name data) :league)
                             (:id path-params)))
                 :start  (fn [_ id]
                           (league-fsm id))})

(reg-chain-named

 :league/load-table
 (fn [{:keys [db]} [id]]
   {:db         (assoc db :fixtures nil
                          :table nil)
    :http-xhrio (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings"))})

 :league/table-received
 (fn [{:keys [db]} [id table]]
   {:db (-> db
            (assoc-in [id :table] (-> table :standings first :table))
            (assoc-in [id :league-name] (-> table :competition :name)))}))

(reg-chain-named
 :league/load-fixtures
 (fn [{:keys [db]} [id]]
   {:http-xhrio (util/http-get (str "https://api.football-data.org/v2/matches")
                               {:params {:competitions id}})})

 :league/fixtures-received
 (fn [{:keys [db]} [_ _ {:keys [matches]}]]
   {:db (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))}))