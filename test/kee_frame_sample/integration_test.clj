(ns kee-frame-sample.integration-test
  (:require [clojure.test :refer :all]
            [etaoin.api :as et]
            [kee-frame-sample.test-util :refer [wait-for input-value verify-input-value click verify-element-text verify-text wait click-href verify-cell]]
            [kee-frame-sample.server :as server]))

(def base-url "http://localhost:3333")

(def ^:dynamic *driver*)

(use-fixtures :each
              (fn [test-fn]
                (let [server (server/run-server 3333)]
                  (try
                    (et/with-phantom
                      {:size [1500 1000]} driver
                      (binding [*driver* driver]
                        (test-fn)))
                    (finally
                      (.stop server))))))

(defn goto [driver url]
  (et/go driver (str base-url "/#" url)))

(defn navigate-to-league [driver league-id]
  (doto driver
    (click {:css "div#app-bar > button"})
    (wait 2)
    (click-href (str "/league/" league-id "/table"))
    (wait 2)))

(deftest hohei
  (testing "Can load live page"
    (doto *driver*
      (goto "/")
      (verify-text "Show only ongoing matches")))
  (testing "Can go to specific league table"
    (doto *driver*
      (goto "/")
      (navigate-to-league 445)
      (verify-text "Premier League")
      (verify-text "Manchester United")
      (verify-text "Manchester City")
      (verify-text "Arsenal")))
  (testing "Can view most recent fixtures for a league"
    (doto *driver*
      (goto "/")
      (navigate-to-league 445)
      (click-href "/league/445/fixtures")
      (verify-text "Date")
      (verify-text "Home")
      (verify-text "Away")))

  (testing "Will load data correctly when using back button"
    (doto *driver*
      (goto "/")
      (navigate-to-league 445)
      (navigate-to-league 455)
      (navigate-to-league 456)
      (verify-text "Juventus")
      (et/back)
      (wait 2)
      (verify-text "Barcelona")
      (et/back)
      (wait 2)
      (verify-text "Manchester United"))))