(ns kee-frame-sample.integration-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as et]
            [kee-frame-sample.test-util :refer [wait-for input-value verify-input-value click verify-element-text verify-text wait click-href verify-cell]]
            [kee-frame-sample.server :as server]))

(def base-url "http://localhost:3333")

(def pause 5)

(def ^:dynamic *driver*)

(use-fixtures :each
              (fn [test-fn]
                (let [server (server/run-server 3333)]
                  (try
                    (et/with-phantom
                      {:size [1000 1000]} driver
                      (binding [*driver* driver]
                        (test-fn)))
                    (finally
                      (.stop server))))))

(defn goto [driver url]
  (et/go driver (str base-url "/#" url)))

(defn navigate-to-league [driver league-id]
  (doto driver
    (click {:css "div#app-bar > button"})
    (wait pause)
    (click-href (str "/#/league/" league-id "/table"))
    (wait pause)))

(defn verify-visible [driver visible? q]
  (is (= visible? (et/visible? driver q))))

(deftest hides-sidebar-on-navigation
  (doto *driver*
    (goto "/")
    (click {:css "div#app-bar > button"})
    (wait pause)
    (click-href "/#/league/2021/table")
    (wait 1)
    (verify-visible false {:tag :a :href "/#/league/2021/table"})))

(deftest showing-only-major-leagues
  (doto *driver*
    (goto "/")
    (click {:css "div#app-bar > button"})
    (wait pause)
    (verify-element-text {:tag :a :href "/#/league/2021/table"} "Premier League")
    (verify-element-text {:tag :a :href "/#/league/2014/table"} "Primera Division")))

(deftest can-load-live-page
  (doto *driver*
    (goto "/")
    (verify-text "Show only ongoing matches")))

(deftest can-go-to-specific-league-table
  (doto *driver*
    (goto "/")
    (navigate-to-league 2021)
    (verify-text "Premier League")
    (verify-text "Manchester United")
    (verify-text "Manchester City")
    (verify-text "Arsenal")))

(deftest will-load-data-correctly-when-using-back-button
  (doto *driver*
    (goto "/")
    (navigate-to-league 2021)
    (navigate-to-league 2014)
    (navigate-to-league 2019)
    (verify-text "Juventus")
    (et/back)
    (wait pause)
    (verify-text "Barcelona")
    (et/back)
    (wait pause)
    (verify-text "Manchester United")))