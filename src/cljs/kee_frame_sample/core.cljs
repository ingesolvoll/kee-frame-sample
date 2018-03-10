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
            [kee-frame-sample.layout :as layout]))

(enable-console-print!)

(defn mount-root []
  (reagent/render [layout/main-panel]
                  (.getElementById js/document "app")))

(def routes ["" {"/"                       :live
                 ["/league/" :id "/" :tab] :league
                 ["/team/" :href]          :team}])

(def initial-db {:drawer-open? false
                 :leagues      nil
                 :fixtures     nil
                 :table        nil
                 :live-matches nil})

(defn ^:export init []
  (kee-frame/start! {:routes     routes
                     :initial-db initial-db})
  (mount-root))