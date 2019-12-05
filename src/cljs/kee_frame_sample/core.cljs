(ns kee-frame-sample.core
  (:require [cljsjs.material-ui]
            [re-frame.core :refer [subscribe]]
            [kee-frame.core :as k]
            [kee-frame-sample.controller.league]
            [kee-frame-sample.controller.leagues]
            [kee-frame-sample.controller.live]
            [kee-frame-sample.controller.common]
            [kee-frame-sample.subscriptions]
            [kee-frame-sample.layout :as layout]
            [cljs.spec.alpha :as s]
            [kee-frame-sample.view.live :as live]
            [kee-frame-sample.view.league :as league]
            [reagent.core :as r]))

(enable-console-print!)

(goog-define debug false)

(defn error-boundary
  [body]
  (let [err-state (r/atom nil)]
    (r/create-class
     {:display-name        "ErrBoundary"
      :component-did-catch (fn [err info]
                             (reset! err-state [err info]))
      :reagent-render      (fn [body]
                             (if (nil? @err-state)
                               body
                               (let [[err info] @err-state]
                                 (js/console.log "******************** err: " err)
                                 [:pre [:code (pr-str info)]])))})))

(defn dispatch-main []
  [error-boundary
   [k/switch-route (comp :name :data)
    :league [league/league-dispatch]
    :live [live/live]
    nil [:div "Loading..."]]])

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
           :debug-config   {:controllers? false
                            :overwrites?  false
                            :events?      true
                            :routes?      false
                            :blacklist    #{:live/tick}}
           :screen         true
           :scroll         false
           :routes         routes
           :hash-routing?  false
           :initial-db     initial-db
           :root-component [layout/main-panel [dispatch-main]]
           :app-db-spec    ::db-spec})