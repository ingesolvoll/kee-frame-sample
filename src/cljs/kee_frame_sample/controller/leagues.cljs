(ns kee-frame-sample.controller.leagues
  (:require [kee-frame.core :as k]
            [kee-frame.fsm.alpha :as fsm]
            [kee-frame-sample.util :as util]
            [re-chain.core :as chain]))

(fsm/reg-no-op :leagues/retry)

(def leagues-fsm
  {:id    :leagues-fsm
   :start ::loading
   :stop  ::loaded
   :fsm   {::loading        {[::fsm/on-enter]      {:dispatch [[:leagues/load]]}
                             [:leagues/loaded]     {:to ::loaded}
                             [:default-on-failure] {:to ::loading-failed}}
           ::loading-failed {[::fsm/timeout 10000] {:to ::loading}
                             [:leagues/retry]    {:to ::loading}}}})

(k/reg-controller :leagues
                {:params (constantly true)
                 :start  (fn []
                           leagues-fsm)})

;; Only show the most interesting ones, with compatible data
(def whitelist #{2021 2014 2019 2015 2002})

(chain/reg-chain-named
 :leagues/load
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/competitions")})

 :leagues/loaded
 (fn [{:keys [db]} [_ leagues]]
   {:db (assoc db :leagues (->> leagues
                                :competitions
                                (filter (comp whitelist :id))))}))