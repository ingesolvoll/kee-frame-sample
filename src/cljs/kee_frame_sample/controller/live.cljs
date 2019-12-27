(ns kee-frame-sample.controller.live
  (:require [kee-frame.fsm :as fsm]
            [kee-frame.core :refer [reg-controller reg-chain-named reg-event-fx reg-event-db]]
            [kee-frame-sample.util :as util]))

(def live-fsm
  {:id    :live-fsm
   :start ::loading
   :fsm   {::loading {[::fsm/on-enter]            {:dispatch [[:live/load-live-matches]]}
                      [:live/loaded-live-matches] {:to ::waiting}
                      [:default-on-failure]       {:to ::error}}
           ::waiting {[::fsm/after 10000] {:to ::loading}}
           ::error   {[::fsm/after 10000] {:to ::loading}}}})

(reg-controller :live-polling
                {:params (fn [route]
                           (when (-> route :data :name (= :live)) true))
                 :start  (fn [] live-fsm)})

(reg-chain-named

 :live/load-live-matches
 (fn [_ _]
   {:http-xhrio (util/http-get "https://api.football-data.org/v20/matches")})

 :live/loaded-live-matches
 (fn [{db :db} [{:keys [matches]}]]
   {:db (assoc db :live-matches matches)}))

(reg-event-db :live/toggle-ongoing
              (fn [db [flag]]
                (assoc db :ongoing-only? flag)))