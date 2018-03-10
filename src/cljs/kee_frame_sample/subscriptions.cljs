(ns kee-frame-sample.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub :drawer-open? :drawer-open?)
(reg-sub :live-matches :live-matches)
(reg-sub :live-match-count (fn [db] (-> db :live-matches count)))
(reg-sub :league-caption :league-caption)
(reg-sub :table :table)
(reg-sub :fixtures :fixtures)
(reg-sub :leagues :leagues)
(reg-sub :team :team)