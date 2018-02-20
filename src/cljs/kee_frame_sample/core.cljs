(ns kee-frame-sample.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [kee-frame-sample.events :as events]
            [kee-frame-sample.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (enable-console-print!)
  (mount-root))
