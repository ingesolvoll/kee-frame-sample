(ns kee-frame-sample.controller.leagues
  (:require [kee-frame.core :as k]
            [kee-frame-sample.util :as util]
            [re-frame.core :as f]))

(def leagues-request-fsm {:id          :leagues
                          :http-xhrio  (util/http-get "https://api.football-data.org/v2/competitions")
                          :max-retries 5
                          :on-success  [::loaded]})

(k/reg-controller :leagues
                  {:params (constantly true)
                   :start  (fn []
                             leagues-request-fsm)})

;; Only show the most interesting ones, with compatible data
(def whitelist #{2021 2014 2019 2015 2002})

(f/reg-event-db ::loaded
  (fn [db [_ leagues]]
    (assoc db :leagues (->> leagues
                            :competitions
                            (filter (comp whitelist :id))))))