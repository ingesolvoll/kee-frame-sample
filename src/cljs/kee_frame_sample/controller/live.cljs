(ns kee-frame-sample.controller.live
  (:require [kee-frame.fsm.alpha :as fsm]
            [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [re-chain.core :as chain]
            [re-frame.core :as f]
            [taoensso.timbre :as log]))

(def retry-in-10-secs {:after [{:delay  10000
                                :target ::loading}]})

(def live-fsm
  {:id      :live
   :initial ::initializing
   :states  {::initializing {:initial ::loading
                             :states  {::error   retry-in-10-secs
                                       ::loading {:entry #(f/dispatch [:live/load-live-matches])
                                                  :on    {:live/loaded-live-matches [:> ::running]
                                                          ::error                   ::error}}}}
             ::running      {:initial ::waiting
                             :states  {::error   retry-in-10-secs
                                       ::waiting retry-in-10-secs
                                       ::loading {:entry #(f/dispatch [:live/load-live-matches])
                                                  :on    {:live/loaded-live-matches ::waiting
                                                          ::error                   ::error}}}}}})

(k/reg-controller :live-polling
                  {:params (fn [route]
                             (when (-> route :data :name (= :live)) true))
                   :start  (fn [] live-fsm)})

(f/reg-sub ::init?
  (fn [_ _]
    (f/subscribe [::fsm/state :live]))
  (fn [state]
    (and (seq? state)
         (= ::initializing (first state)))))


(chain/reg-chain-named

 :live/load-live-matches
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/matches"
                               {:on-failure [:live/transition ::error]})})

 :live/loaded-live-matches
 (fn [{db :db} [_ {:keys [matches]}]]
   {:db       (assoc db :live-matches matches)
    :dispatch [:live/transition :live/loaded-live-matches]}))

(f/reg-event-db :live/toggle-ongoing
              (fn [db [_ flag]]
                (assoc db :ongoing-only? flag)))