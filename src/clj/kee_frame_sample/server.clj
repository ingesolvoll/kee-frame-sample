(ns kee-frame-sample.server
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defroutes app
           (route/resources "/" {:root "public"})
           (GET "*" [] (-> (response/resource-response "index.html" {:root "public"})
                           (response/content-type "text/html"))))

(defn run-server [port]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty #'app {:port port :join? false})))

(defn -main [& [port]]
  (run-server port))
