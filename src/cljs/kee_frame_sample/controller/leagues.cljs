(ns kee-frame-sample.controller.leagues
  (:require [kee-frame.core :refer [reg-controller reg-chain reg-event-fx reg-event-db]]
            [kee-frame-sample.util :as util]))

(reg-controller :leagues
                {:params (constantly true)
                 :start  [:leagues/load]})

;; Only show the most interesting ones, with compatible data
(def whitelist #{2021 2014 2013 2015 2002})

(reg-chain :leagues/load

           (fn [_ _]
             {:http-xhrio (util/http-get "http://api.football-data.org/v2/competitions")})

           (fn [{:keys [db]} [_ leagues]]
             {:db (assoc db :leagues (->> leagues
                                          :competitions
                                          (filter (comp whitelist :id))))}))