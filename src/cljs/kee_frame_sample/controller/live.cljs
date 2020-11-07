(ns kee-frame-sample.controller.live
  (:require [kee-frame.fsm.alpha :as fsm]
            [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [re-chain.core :as chain]
            [re-frame.core :as f]
            [taoensso.timbre :as log]))

(defn waiting-state [next]
  {:after [{:delay  10000
            :target next}]})

(defn loading-state [error] {:entry (fn [state event]
                                      (js/console.log "******************** state event: " state event)
                                      (f/dispatch [:live/load-live-matches]))
                             :on    {:live/loaded-live-matches ::waiting
                                     :default-on-failure       error}})

(def live-fsm
  {:id      :live-fsm
   :initial ::init
   :states  {::init       (loading-state ::init-error)
             ::loading    (loading-state ::error)
             ::waiting    (waiting-state ::loading)
             ::error      (waiting-state ::loading)
             ::init-error (waiting-state ::init)}})

(k/reg-controller :live-polling
                  {:params (fn [route]
                             (when (-> route :data :name (= :live)) true))
                   :start  (fn [] live-fsm)})

(f/reg-sub ::init?
  (fn [_ _]
    (f/subscribe [::fsm/state live-fsm]))
  (fn [state]
    (#{::init
       ::init-error} state)))


(chain/reg-chain-named

 :live/load-live-matches
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v2/matches")})

 :live/loaded-live-matches
 (fn [{db :db} [_ {:keys [matches]}]]
   {:db (assoc db :live-matches matches)}))

(f/reg-event-db :live/toggle-ongoing
              (fn [db [_ flag]]
                (assoc db :ongoing-only? flag)))