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

(reg-sub :live-matches
         (fn [db _]
           (->> db
                :live-matches
                (map #(update % :date format/format-time))
                (map (fn [match]
                       (let [league-name (-> match
                                             :_links
                                             :competition
                                             :href
                                             (str/split #"/")
                                             last
                                             (find-league-name (:leagues db))
                                             )]
                         (assoc match :league-name league-name))))
                (group-by :league-name))))