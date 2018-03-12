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

(deftest hohei
  (testing "Can load live page"
    (doto *driver*
      (goto "/")
      (verify-text "Show only ongoing matches")))
  (testing "Can go to specific league"
    (doto *driver*
      (goto "/")
      (click {:css "div#app-bar > button"})
      (wait 2)
      (click-href "/league/445/table")
      (wait 2)
      (verify-text "Manchester United")
      (verify-text "Manchester City")
      (verify-text "Arsenal"))))