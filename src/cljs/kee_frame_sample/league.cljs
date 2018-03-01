(ns kee-frame-sample.league
  (:require [kee-frame.core :refer [reg-controller] :as k]
            [re-frame.core :refer [reg-event-fx reg-event-db reg-sub debug]]
            [ajax.core :as ajax]))

#_(reg-event-chain :league/load
                   [:fx {:http-xhrio {:method          :get
                                      :uri             (str "http://api.football-data.org/v1/competitions/" [::k/params 1] "/fixtures")
                                      :params          {:timeFrame         :p7
                                                        :some-other-filter [::k/ctx :db :filter :filter-type]}
                                      :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                                      :on-failure      [::k/failure] ;; or just another shared [:your-handler-event]
                                      :response-format (ajax/json-response-format)
                                      :on-success      [::k/next]}}]
                   [:db [[:table [::k/params 1]]]]
                   [:failure {:navigate-to "/error/page"
                              :db          [:error :message (str "oh-noes, did not end well that one: " [::k/params 1])]}])

(reg-controller :league
                {:params (fn [{:keys [handler route-params]}]
                           (when (= handler :league)
                             route-params))
                 :start  (fn [_ {:keys [id tab]}]
                           (case tab
                             "table" [:league/load-table id]
                             "fixtures" [:league/load-fixtures id]))})

(reg-event-fx :league/load-fixtures
              [debug]
              (fn [_ [_ id]]
                {:http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/competitions/" id "/fixtures")
                              :params          {:timeFrame :p7}
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)
                              :on-success      [:league/fixtures-loaded]}}))

(reg-event-fx :league/load-table
              [debug]
              (fn [_ [_ id]]
                {:http-xhrio {:method          :get
                              :uri             (str "http://api.football-data.org/v1/competitions/" id "/leagueTable")
                              ;:params          {:matchday 3}
                              :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
                              :on-failure      [:log-error]
                              :response-format (ajax/json-response-format)
                              :on-success      [:league/table-loaded]}}))

(reg-event-db :league/table-loaded
              [debug]
              (fn [db [_ league]]
                (assoc db :table league)))

(reg-event-db :league/fixtures-loaded
              [debug]
              (fn [db [_ league]]
                (assoc db :fixtures league)))

(reg-sub :table (fn [db] (some-> db :table)))
(reg-sub :fixtures (fn [db] (some-> db :fixtures)))