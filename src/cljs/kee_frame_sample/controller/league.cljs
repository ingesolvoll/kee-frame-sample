(ns kee-frame-sample.controller.league
  (:require [kee-frame.core :refer [reg-controller reg-chain-named]]
            [kee-frame-sample.util :as util]
            [kee-frame-sample.format :as format]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             (:id route-params)))
                 :start  (fn [_ id]
                           [:league/load id])})

(reg-chain-named

  :league/load
  (fn [{:keys [db]} [id]]
    {:db         (assoc db :fixtures nil
                           :table nil)
     :http-xhrio (util/http-get (str "http://api.football-data.org/v1/competitions/" id "/leagueTable"))})

  :league/load-fixtures
  (fn [{:keys [db]} [id table]]
    {:db         (assoc db :table (:standing table)
                           :league-caption (:leagueCaption table))
     :http-xhrio (util/http-get (str "http://api.football-data.org/v1/competitions/" id "/fixtures")
                                {:params {:timeFrame :p7}})})

  :league/save-fixtures
  (fn [{:keys [db]} [_ _ {:keys [fixtures]}]]
    {:db (assoc db :fixtures (map #(update % :date format/format-date) fixtures))}))