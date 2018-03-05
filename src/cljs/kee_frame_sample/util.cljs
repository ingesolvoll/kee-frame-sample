(ns kee-frame-sample.util
  (:require [ajax.core :as ajax]))

(defn http-get [params]
  (merge {:method          :get
          :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
          :response-format (ajax/json-response-format)}
         params))