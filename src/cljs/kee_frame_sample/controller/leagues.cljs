(ns kee-frame-sample.controller.leagues
  (:require [kee-frame.core :as k]
            [kee-frame.fsm.alpha :as fsm]
            [kee-frame-sample.util :as util]
            [re-chain.core :as chain]
            [re-frame.core :as f]))

(def leagues-fsm
  {:id      :leagues
   :initial ::loading
   :states  {::loading        {:entry #(f/dispatch [:leagues/load])
                               :on    {::loaded ::loaded
                                       ::error  ::loading-failed}}
             ::loading-failed {:after [{:delay  3000
                                        :target ::loading}]
                               :on    {:leagues/retry ::loading}}
             ::loaded         {}}})

(k/reg-controller :leagues
                  {:params (constantly true)
                   :start  (fn []
                             leagues-fsm)})

;; Only show the most interesting ones, with compatible data
(def whitelist #{2021 2014 2019 2015 2002})

(chain/reg-chain
 :leagues/load
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/competitions"
                               {:on-failure [:leagues/transition ::error]})})

 (fn [{:keys [db]} [_ leagues]]
   {:db       (assoc db :leagues (->> leagues
                                      :competitions
                                      (filter (comp whitelist :id))))
    :dispatch [:leagues/transition ::loaded]}))