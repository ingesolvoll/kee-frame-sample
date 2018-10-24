(ns kee-frame-sample.core
  (:require [cljsjs.material-ui]
            [re-frame.core :refer [subscribe]]
            [kee-frame.core :as k]
            [kee-frame-sample.controller.league]
            [kee-frame-sample.controller.leagues]
            [kee-frame-sample.controller.live]
            [kee-frame-sample.controller.common]
            [kee-frame-sample.subscriptions]
            [kee-frame-sample.routers :as routers]
            [kee-frame-sample.layout :as layout]
            [cljs.spec.alpha :as s]
            [kee-frame-sample.view.live :as live]
            [ajax.core :as ajax]
            [kee-frame-sample.view.league :as league]))

(enable-console-print!)

(goog-define debug false)

(defn route-interceptors [route]
  (let [connection-balance (atom 0)
        replace-interceptor (fn [interceptors]
                              (conj (filter #(not= "route-interceptor" (:name %)) interceptors)
                                    (ajax/to-interceptor {:name     "route-interceptor"
                                                          :request  (fn [request]
                                                                      (swap! connection-balance inc)
                                                                      (println "CONN BAL REQ " @connection-balance)
                                                                      request)
                                                          :response (fn [response]
                                                                      (swap! connection-balance dec)
                                                                      (println "CONN BAL RES " @connection-balance)
                                                                      response)})))]
    (swap! ajax/default-interceptors replace-interceptor)
    connection-balance))

(defn dispatch-main []
  [k/switch-route (comp :name :data)
   :league [league/league-dispatch]
   :live [live/live]
   nil [:div "Loading..."]])

(def routes [["/" :live]
             ["/league/:id/:tab" :league]])

(def initial-db {:drawer-open?  false
                 :leagues       nil
                 :fixtures      nil
                 :table         nil
                 :live-matches  nil
                 :ongoing-only? false})

(s/def ::league (s/keys :req-un [::name ::id]))
(s/def ::leagues (s/nilable (s/coll-of ::league)))
(s/def ::db-spec (s/keys :req-un [::drawer-open? ::leagues ::fixtures ::table ::live-matches ::ongoing-only?]))

(k/start! {:debug?         debug
           :screen         true
           :routes         routes
           :hash-routing?  true
           :initial-db     initial-db
           :root-component [layout/main-panel [dispatch-main]]
           :app-db-spec    ::db-spec})

(route-interceptors nil)