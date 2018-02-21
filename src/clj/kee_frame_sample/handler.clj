(ns kee-frame-sample.handler
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as response]))



(defroutes app
           (route/resources "/" {:root "public"})
           (GET "*" [] (-> (response/resource-response "index.html" {:root "public"})
                           (response/content-type "text/html"))))