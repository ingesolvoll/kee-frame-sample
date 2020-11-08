(ns kee-frame-sample.controller.live
  (:require [kee-frame.fsm.alpha :as fsm]
            [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [re-chain.core :as chain]
            [re-frame.core :as f]
            [statecharts.core :as sc]
            [taoensso.timbre :as log]))

(def live-fsm
  {:id      :live
   :initial ::initializing
   :states  {::initializing {:initial ::loading
                             :states  {::error   {:after [{:delay  10000
                                                           :target ::loading}]}
                                       ::loading {:entry #(f/dispatch [:live/load-live-matches])}}
                             :on      {::error                   [:. ::error]
                                       :live/loaded-live-matches [::running ::waiting]}}
             ::running      {:states {::error   {:after [{:delay  10000
                                                          :target ::loading}]}
                                      ::waiting {:entry (sc/assign #(assoc % :last-update (js/Date.)))
                                                 :after [{:delay  10000
                                                          :target ::loading}]}
                                      ::loading {:entry #(f/dispatch [:live/load-live-matches])}}
                             :on     {::error                   [:. ::error]
                                      :live/loaded-live-matches [:. ::waiting]}}}})

(k/reg-controller :live-polling
                  {:params (fn [route]
                             (when (-> route :data :name (= :live)) true))
                   :start  (fn [] live-fsm)})

(f/reg-sub ::init?
  (fn [_ _]
    (f/subscribe [::fsm/state :live]))
  (fn [state]
    (#{::init
       ::init-error} state)))


(chain/reg-chain-named

 :live/load-live-matches
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/matches"
                               {:on-failure [:live/fsm-event ::error]})})

 :live/loaded-live-matches
 (fn [{db :db} [_ {:keys [matches]}]]
   {:db       (assoc db :live-matches matches)
    :dispatch [:live/fsm-event :live/loaded-live-matches]}))

(f/reg-event-db :live/toggle-ongoing
              (fn [db [_ flag]]
                (assoc db :ongoing-only? flag)))