(ns kee-frame-sample.subscriptions
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [clojure.string :as str]
            [kee-frame-sample.format :as format]))

(reg-sub :drawer-open?
         (fn [db]
           (get db :drawer-open? false)))
(reg-sub :live-match-count (comp count :live-matches))
(reg-sub :league-name
         (fn [db [_ id]]
           (get-in db [id :league-name])))
(reg-sub :league-id
         (fn []
           (subscribe [:kee-frame/route]))
         (fn [route _]
           (-> route :path-params :id)))

(reg-sub :table
         (fn [db [_ id]]
           (get-in db [id :table])))

(reg-sub :fixtures :fixtures)
(reg-sub :leagues :leagues)

(defn ongoing-filterer [ongoing-only? {:keys [status]}]
  (or (not ongoing-only?)
      (= status "IN_PLAY")))

(reg-sub :live-matches
         (fn [db _]
           (some->> db
                    :live-matches
                    (map #(update % :utcDate format/format-time))
                    (filter (partial ongoing-filterer (:ongoing-only? db)))
                    (group-by :competition)
                    (filter (comp identity first))
                    (map (fn [[k matches]]
                           [k (sort-by (juxt :utcDate (comp :name :homeTeam)) matches)]))
                    (into {}))))
