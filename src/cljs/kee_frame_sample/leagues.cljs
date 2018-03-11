(ns kee-frame-sample.leagues
  (:require [kee-frame.core :refer [reg-controller reg-chain]]
            [re-frame.core :refer [reg-event-fx reg-fx reg-event-db reg-sub debug]]
            [kee-frame-sample.util :as util]))

(reg-controller :leagues
                {:params (constantly true)
                 :start  [:leagues/load]})

(reg-event-fx :leagues/select
              (fn [_ [_ league-id]]
                {:navigate-to [:league :id league-id :tab :table]}))

;; ONly show the most interesting ones, with compatible data
(def whitelist #{445 446 449 450 452 455 456})

(reg-chain :leagues/load

           (fn [_ _]
             {:http-xhrio (util/http-get "http://api.football-data.org/v1/competitions/?season=2017")})

           (fn [{:keys [db]} [_ _ leagues]]
             {:db (assoc db :leagues (filter (comp whitelist :id) leagues))}))