(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :as k]
            [re-chain.core :as chain]
            [kee-frame-sample.util :as util]
            [kee-frame.fsm.alpha :as fsm]
            [kee-frame-sample.format :as format]
            [re-frame.core :as f]))

(defn league-fsm [id]
  {:id      :league
   :initial ::table
   :states  {::table    {:initial ::loading
                         :states  {::loading {:entry #(f/dispatch [:league/load-table id])}
                                   ::error   {:after [{:delay  3000
                                                       :target [:> ::table]}]}}
                         :on      {:league/table-received ::fixtures
                                   ::error                [:. ::error]}}
             ::fixtures {:initial ::loading
                         :states  {::loading {:entry #(f/dispatch [:league/load-fixtures id])}
                                   ::error   {:after [{:delay  3000
                                                       :target [:> ::fixtures]}]}}
                         :on      {:league/fixtures-received ::loaded
                                   ::error                   [:. ::error]}}
             ::loaded   {}}})

(f/reg-sub ::state
  (fn [[_ id]]
    (f/subscribe [::fsm/state :league]))
  identity)

(f/reg-sub ::failed?
           (fn [_ [_ id]]
             (f/subscribe [::state id]))
           (fn [state]
             (#{::loading-table-failed
                ::loading-fixtures-failed} state)))

(k/reg-controller :league
                  {:params (fn [{:keys [data path-params]}]
                             (when (= (:name data) :league)
                               (:id path-params)))
                   :start  (fn [_ id]
                             (league-fsm id))})

(chain/reg-chain-named

 :league/load-table
 (fn [{:keys [db]} [_ id]]
   {:db         (assoc db :fixtures nil
                          :table nil)
    :http-xhrio (util/http-get (str "https://api.football-data.org/v2/competitions/" id "/standings")
                               {:on-failure [:league/transition ::error]})})

 :league/table-received
 (fn [{:keys [db]} [_ id table]]
   {:db       (-> db
                  (assoc-in [id :table] (-> table :standings first :table))
                  (assoc-in [id :league-name] (-> table :competition :name)))
    :dispatch [:league/transition :league/table-received]}))

(chain/reg-chain-named
 :league/load-fixtures
 (fn [{:keys [db]} [_ id]]
   {:http-xhrio (util/http-get (str "https://api.football-data.org/v2/matches")
                               {:params     {:competitions id}
                                :on-failure [:league/transition ::error]})})

 :league/fixtures-received
 (fn [{:keys [db]} [_ _ {:keys [matches]}]]
   {:db       (assoc db :fixtures (map #(update % :utcDate format/format-date) matches))
    :dispatch [:league/transition :league/fixtures-received]}))