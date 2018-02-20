(ns kee-frame-sample.views
  (:require [re-frame.core :as re-frame]
            [kee-frame-sample.subs :as subs]
            ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div "Hello from " @name]))
