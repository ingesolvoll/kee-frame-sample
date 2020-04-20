(ns kee-frame-sample.controller.leagues
  (:require [kee-frame.core :refer [reg-controller reg-chain-named reg-event-fx reg-event-db]]
            [kee-frame.fsm.alpha :as fsm]
            [kee-frame-sample.util :as util]))

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

(reg-controller :leagues
                {:params (constantly true)
                 :start  (fn []
                           leagues-fsm)})

;; Only show the most interesting ones, with compatible data
(def whitelist #{2021 2014 2019 2015 2002})

(reg-chain-named
 :leagues/load
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/competitions")})

 :leagues/loaded
 (fn [{:keys [db]} [leagues]]
   {:db (assoc db :leagues (->> leagues
                                :competitions
                                (filter (comp whitelist :id))))}))