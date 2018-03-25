(ns kee-frame-sample.subscriptions
  (:require [re-frame.core :refer [reg-sub]]
            [clojure.string :as str]
            [kee-frame-sample.format :as format]))

(reg-sub :drawer-open? :drawer-open?)
(reg-sub :live-match-count (fn [db] (-> db :live-matches count)))
(reg-sub :league-caption :league-caption)
(reg-sub :table :table)
(reg-sub :fixtures :fixtures)
(reg-sub :leagues :leagues)
(reg-sub :team :team)

(defn find-league-name [id-str leagues]
  (->> leagues
       (filter #(= id-str (str (:id %))))
       first
       :caption))

(defn ongoing-filterer [ongoing-only? {:keys [status]}]
  (or (not ongoing-only?)
      (= status "IN_PLAY")))

(defn assoc-league-name [leagues match]
  (assoc match :league-name (-> match
                                :_links
                                :competition
                                :href
                                (str/split #"/")
                                last
                                (find-league-name leagues))))

(reg-sub :live-matches
         (fn [db _]
           (some->> db
                    :live-matches
                    (map #(update % :date format/format-time))
                    (map (partial assoc-league-name (:leagues db)))
                    (filter (partial ongoing-filterer (:ongoing-only? db)))
                    (group-by :league-name)
                    (filter (comp identity first))
                    (into {}))))