(ns kee-frame-sample.util
  (:require [ajax.core :as ajax]))

(defn http-get
  ([uri] (http-get uri nil))
  ([uri params]
   (-> {:method          :get
        :headers         {"X-Auth-Token" "974c0523d8964af590d3bb9d72b45d0a"}
        :response-format (ajax/json-response-format {:keywords? true})}
       (merge params)
       (assoc :uri uri))))