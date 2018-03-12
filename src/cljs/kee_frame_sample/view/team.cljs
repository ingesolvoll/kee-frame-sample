(ns kee-frame-sample.view.team
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn team []
  (if-let [{:keys [teamName]} @(subscribe [:team])]
    [:div
     [:h1 teamName]
     ]))