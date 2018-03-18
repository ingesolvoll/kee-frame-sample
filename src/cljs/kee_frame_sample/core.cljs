(ns kee-frame-sample.core
  (:require [cljsjs.material-ui]
            [day8.re-frame.http-fx]
            [re-frame.core :refer [subscribe]]
            [kee-frame.core :as kee-frame]
            [kee-frame-sample.controller.league]
            [kee-frame-sample.controller.team]
            [kee-frame-sample.controller.leagues]
            [kee-frame-sample.controller.live]
            [kee-frame-sample.controller.common]
            [kee-frame-sample.subscriptions]
            [kee-frame-sample.layout :as layout]
            [cljs.spec.alpha :as s]
            [kee-frame-sample.view.live :as live]
            [kee-frame-sample.view.team :as team]
            [kee-frame-sample.view.league :as league]
            [bidi.bidi :as bidi]
            [accountant.core :as accountant]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))

(enable-console-print!)

(goog-define debug false)
(goog-define use-framework true)

(defn dispatch-main []
  (case (:handler @(subscribe [:kee-frame/route]))
    :league [league/league-dispatch]
    :team [team/team]
    :live [live/live]
    [:div "Loading..."]))

(def routes ["" {"/"                       :live
                 ["/league/" :id "/" :tab] :league
                 ["/team/" :href]          :team}])

(def initial-db {:drawer-open?  false
                 :leagues       nil
                 :fixtures      nil
                 :table         nil
                 :live-matches  nil
                 :ongoing-only? false})

(s/def ::league (s/keys :req-un [::caption ::id]))
(s/def ::leagues (s/nilable (s/coll-of ::league)))
(s/def ::db-spec (s/keys :req-un [::drawer-open? ::leagues ::fixtures ::table ::live-matches ::ongoing-only?]))

(defn manual-init []

  (accountant/configure-navigation!
    {:nav-handler  (fn [path]
                     (if-let [route (->> path
                                         (bidi/match-route routes))]
                       (rf/dispatch [:kee-frame.router/route-changed route])))
     :path-exists? #(boolean (bidi/match-route routes %))})
  (kee-frame/start! {:debug?     debug
                     :initial-db initial-db})
  (reagent/render [layout/main-panel [dispatch-main]]
                  (.getElementById js/document "app"))
  (accountant/dispatch-current!))

(if use-framework
  (kee-frame/start! {:debug?         debug
                     :routes         routes
                     :initial-db     initial-db
                     :root-component [layout/main-panel [dispatch-main]]
                     :app-db-spec    ::db-spec})
  (manual-init))