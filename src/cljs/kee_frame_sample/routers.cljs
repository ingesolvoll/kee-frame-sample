(ns kee-frame-sample.routers
  (:require [reitit.core :as reitit]
            [kee-frame.api :as api]
            [router.core :as keechma]
            [bide.core :as bide]
            [clojure.string :as str]
            [bidi.bidi :as bidi]))

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


(defn assert-route-data [data]
  (when-not (vector? data)
    (throw (ex-info "Bidi route data is a vector consisting of handler and route params as kw args"
                    {:route-data data}))))

(defn url-not-found [routes data]
  (throw (ex-info "Could not find url for the provided data"
                  {:routes routes
                   :data   data})))

(defn route-match-not-found [routes url]
  (throw (ex-info "No match for URL in routes"
                  {:url    url
                   :routes routes})))

(defrecord BidiBrowserRouter [routes]
  api/Router
  (data->url [_ data]
    (assert-route-data data)
    (or (apply bidi/path-for routes data)
        (url-not-found routes data)))
  (url->data [_ url]
    (if-let [match (bidi/match-route routes url)]
      (assoc match :path url)
      (route-match-not-found routes url))))

(defrecord BidiHashRouter [routes]
  api/Router
  (data->url [_ data]
    (assert-route-data data)
    (or (str "/#" (apply bidi/path-for routes data))
        (url-not-found routes data)))
  (url->data [_ url]
    (let [[path+query fragment] (-> url (str/replace #"^/#" "") (str/split #"#" 2))
          [path query] (str/split path+query #"\?" 2)]
      (some-> (or (bidi/match-route routes path)
                  (route-match-not-found routes url))
              (assoc :query-string query :hash fragment)))))

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
