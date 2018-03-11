(ns kee-frame-sample.core
  (:require [cljsjs.material-ui]
            [reagent.core :as reagent]
            [re-frame.core :refer [reg-sub]]
            [day8.re-frame.http-fx]
            [kee-frame.core :as kee-frame]
            [kee-frame-sample.league]
            [kee-frame-sample.team]
            [kee-frame-sample.leagues]
            [kee-frame-sample.live]
            [kee-frame-sample.events]
            [kee-frame-sample.subscriptions]
            [kee-frame-sample.layout :as layout]
            [cljs.spec.alpha :as s]))

(enable-console-print!)

(defn mount-root []
  (reagent/render [layout/main-panel]
                  (.getElementById js/document "app")))

(def routes ["" {"/"                       :live
                 ["/league/" :id "/" :tab] :league
                 ["/team/" :href]          :team}])

(def initial-db {:drawer-open?  false
                 :leagues       nil
                 :fixtures      nil
                 :table         nil
                 :live-matches  nil
                 :ongoing-only? false})

(s/def ::league (s/keys :req-un [::caption ::id]))
(s/def ::leagues (s/nilable (s/coll-of ::league)))
(s/def ::db-spec (s/keys :req-un [::drawer-open? ::leagues ::fixtures ::table ::live-matches ::ongoing-only?]))

(defn ^:export init []
  (kee-frame/start! {:debug?      true
                     :routes      routes
                     :initial-db  initial-db
                     :app-db-spec ::db-spec})
  (mount-root))