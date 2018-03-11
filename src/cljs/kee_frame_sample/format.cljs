(ns kee-frame-sample.format
  (:require [cljs-time.format :as tf]
            [cljs-time.core :as time]))

(defn format-time [d]
  (->> d
       (js/Date.)
       time/to-default-time-zone
       (tf/unparse (tf/formatter "HH:mm"))))

(defn format-date [d]
  (->> d
       (js/Date.)
       time/to-default-time-zone
       (tf/unparse (tf/formatter "dd.MM HH.mm"))))