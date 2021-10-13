(ns kee-frame-sample.controller.live
  (:require [glimt.core :as http]
            [kee-frame-sample.util :as util]
            [kee-frame.core :as k]
            [kee-frame.fsm.beta :as fsm]
            [re-frame.core :as f]
            [statecharts.core :as sc]))

(defn calculate-backoff
  "Exponential backoff, with a upper limit of 15 seconds."
  [state & _]
  (-> (js/Math.pow 2 (:retries state))
      (* 1000)
      (min 15000)))

(defn update-retries [state & _]
  (update state :retries inc))

(defn reset-retries [state & _]
  (assoc state :retries 0))

(def error-loop {:entry (sc/assign update-retries)
                 :after [{:delay  calculate-backoff
                          :target ::loading}]})

(def live-matches-loader
  {:id               :live-matches-loader
   :transition-event ::transition
   :http-xhrio       (util/http-get "https://api.football-data.org/v2/matches")
   :on-success       [:live/loaded-live-matches]
   :retry-delay      calculate-backoff
   :success-state    [:> ::running ::waiting]
   :error-state      [:> ::running ::error]})

(def live-fsm
  {:id               :live
   :transition-event ::transition
   :initial          ::running
   :states           {::running {:initial ::loading
                                 :states  {::error   error-loop
                                           ::waiting {:entry (sc/assign reset-retries)
                                                      :after [{:delay  10000
                                                               :target ::loading}]}
                                           ::loading (http/http-fsm-embedded live-matches-loader)}}}})

(k/reg-controller :live-polling
                  {:params (fn [route]
                             (when (-> route :data :name (= :live)) true))
                   :start  (fn [] [::fsm/start live-fsm])})

(f/reg-sub ::init?
  (fn [_ _]
    (f/subscribe [::fsm/state :live]))
  (fn [state]
    (and (seq? state)
         (= ::initializing (first state)))))

(f/reg-event-db :live/loaded-live-matches
                (fn [db [_ {:keys [matches]}]]
                  (assoc db :live-matches matches)))


(f/reg-event-db :live/toggle-ongoing
              (fn [db [_ flag]]
                (assoc db :ongoing-only? flag)))
