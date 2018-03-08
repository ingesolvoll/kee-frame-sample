(ns kee-frame-sample.live
  (:require-macros [kee-frame.chain :refer [reg-chain]])
  (:require [re-interval.core :refer [register-interval-handlers]]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]
            [kee-frame.core :refer [reg-controller] :as k]
            [cljs-time.core :as time]
            [cljs-time.format :as tf]
            [kee-frame-sample.format :as format]))

(register-interval-handlers :live nil 5000)

(reg-controller :live-polling
                {:params (fn [{:keys [handler]}]
                           (when (= handler :live) true))
                 :start  [:live/start]
                 :stop   [:live/stop]})

(reg-controller :live-startup
                {:params (constantly true)
                 :start  [:live/load-matches]})

(reg-event-fx :live/tick
              (fn [_ _] {:dispatch [:live/load-matches true]}))

(defn process-fixtures [fixtures]
  (->> fixtures
       :fixtures
       (map #(update % :date format/format-date))))

(reg-chain :live/load-matches
           {:http-xhrio {:method          :get
                         :uri             "http://api.football-data.org/v1/fixtures"
                         :params          {:timeFrame :n1}
                         :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                         :on-failure      [:log-error]
                         :response-format (ajax/json-response-format {:keywords? true})}}
           {:db [[:live-matches [::k/params 1 process-fixtures]]]})