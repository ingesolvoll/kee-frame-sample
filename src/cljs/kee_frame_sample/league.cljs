(ns kee-frame-sample.league
  (:require [kee-frame.core :refer [reg-controller]]
            [kee-frame.chain :refer [reg-chain-2]]
            [kee-frame-sample.util :refer [http-get]]
            [kee-frame-sample.format :as format]))

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             (:id route-params)))
                 :start  (fn [_ id]
                           [:league/load id])})

(reg-chain-2 :league/load
             (fn [{:keys [db]} [_ id]]
               {:db         (assoc db :fixtures nil
                                      :table nil)
                :http-xhrio (http-get (str "http://api.football-data.org/v1/competitions/" id "/leagueTable"))})

             (fn [{:keys [db]} [_ id table]]
               {:db         (assoc db :table (:standing table)
                                      :league-caption (:leagueCaption table))
                :http-xhrio (http-get (str "http://api.football-data.org/v1/competitions/" id "/fixtures")
                                      {:params {:timeFrame :p7}})})

             (fn [{:keys [db]} [_ _ _ {:keys [fixtures]}]]
               {:db (assoc db :fixtures (map #(update % :date format/format-date) fixtures))}))