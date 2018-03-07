(ns kee-frame-sample.subscriptions
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub :drawer-open? :drawer-open?)
(reg-sub :live-matches :live-matches)