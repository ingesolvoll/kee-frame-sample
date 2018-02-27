(defproject kee-frame-sample "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.8.0-alpha2"]
                 [re-frame "0.10.5"]
                 [kee-frame "0.0.1-SNAPSHOT"]
                 [day8.re-frame/http-fx "0.1.5"]
                 [ring "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [compojure "1.5.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [environ "1.0.0"]
                 [re-interval "0.0.1"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs     ["resources/public/css"]
             :ring-handler kee-frame-sample.server/app}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [day8.re-frame/re-frame-10x "0.2.0-react16"]]
    :plugins      [[lein-figwheel "0.5.13"]]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs"]
                :figwheel     {:on-jsload "kee_frame_sample.core/mount-root"}
                :compiler     {:main                 kee-frame-sample.core
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :asset-path           "/js/compiled/out"
                               :source-map-timestamp true
                               :preloads             [devtools.preload day8.re-frame-10x.preload]
                               :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                               :external-config      {:devtools/config {:features-to-install :all}}}}]})
