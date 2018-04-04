(ns kee-frame-sample.routers
  (:require [reitit.core :as reitit]
            [kee-frame.api :as api]
            [router.core :as keechma]
            [bide.core :as bide]))

(defrecord BideRouter [routes]
  api/Router
  (data->url [_ data] (apply bide/resolve routes data))
  (url->data [_ url] (let [[handler route-params] (bide/match routes url)]
                       {:handler      handler
                        :route-params route-params})))

(defrecord KeechmaRouter [routes]
  api/Router
  (data->url [_ data] (keechma/map->url routes data))
  (url->data [_ url] (let [a (keechma/url->map routes url)]
                       (println a)
                       a)))

(defrecord ReititRouter [routes]
  api/Router
  (data->url [_ data] (apply reitit/match-by-name routes data))
  (url->data [_ url] (reitit/match-by-path routes url)))

(def bide-routes
  (bide/router [["/" :live]
                ["/league/:id/:tab" :league]]))

(def keechma-routes
  [["/:handler" {:handler :live}]
   "/:handler/:id/:tab"])

(def reitit-routes
  (reitit/router
    [["/" :live]
     ["/league/:id/:tab" :league]]))