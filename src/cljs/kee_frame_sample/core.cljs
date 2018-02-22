(ns kee-frame-sample.core
  (:require [reagent.core :as reagent]
            [day8.re-frame.http-fx]
            [kee-frame.core :as kee-frame]
            [kee-frame-sample.league]
            [kee-frame-sample.leagues]
            [kee-frame-sample.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (kee-frame/start! ["" {"/"              :index
                         ["/league/" :id] :league}])
  (enable-console-print!)
  (mount-root))