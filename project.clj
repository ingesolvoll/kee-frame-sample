(defproject kee-frame-sample "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.8.0-alpha2"]
                 [re-frame "0.10.3-alpha1" :exclusions [reagent]]
                 [kee-frame "0.0.1-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]]
    :plugins      [[lein-figwheel "0.5.13"]]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs"]
                :figwheel     {:on-jsload "kee-frame-sample.core/mount-root"}
                :compiler     {:main                 kee-frame-sample.core
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :asset-path           "js/compiled/out"
                               :source-map-timestamp true
                               :preloads             [devtools.preload]
                               :external-config      {:devtools/config {:features-to-install :all}}}}]})
