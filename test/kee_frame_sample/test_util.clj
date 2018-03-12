(ns kee-frame-sample.test-util
  (:require [clojure.test :refer :all]
            [etaoin.api :as et]))

(defn wait-for [driver element-key]
  (et/wait-visible driver element-key))

(defn input-value [driver q]
  (wait-for driver q)
  (et/get-element-value driver q))

(defn verify-input-value [driver q value]
  (wait-for driver q)
  (is (= value (input-value driver q))))

(defn click [driver q]
  (wait-for driver q)
  (et/click driver q))

(defn verify-element-text [driver q text]
  (wait-for driver q)
  (is (= text (et/get-element-text driver q))))

(defn verify-text [driver text]
  (is (et/has-text? driver text)))

(defn wait [_ seconds]
  (Thread/sleep (* seconds 1000)))

(defn click-href [driver url]
  (click driver (str "//a[@href='" url "']")))

(defn verify-cell [driver table-id x y text]
  (let [xpath (str "//table[@id='" table-id "']/tbody/tr[" x "]/td[" y "]")]
    (verify-element-text driver xpath text)))