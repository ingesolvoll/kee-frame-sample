(ns kee-frame-sample.events
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    {}))
