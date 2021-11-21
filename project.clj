(defproject kee-frame-sample "0.1.0-SNAPSHOT"
  :dependencies [[thheller/shadow-cljs "2.15.12"]
                 [kee-frame "1.3.2"]
                 [glimt "0.2.2"]
                 [day8.re-frame/http-fx "0.1.5"]
                 [cljsjs/material-ui "0.19.0-0"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [sablono "0.8.0"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/cljs"]

  :test-paths ["test"]

  :uberjar-name "kee-frame-sample.jar"

  :main kee-frame-sample.server

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:server  {:source-paths ["src/clj"]
                       :dependencies [[org.clojure/clojure "1.10.3"]
                                      [ring "1.9.4"]
                                      [ring/ring-defaults "0.3.3"]
                                      [compojure "1.6.2"]
                                      [environ "1.0.0"]
                                      [ring/ring-jetty-adapter "1.9.4"]]}
             :test    {:source-paths ["test"]
                       :dependencies [[etaoin "0.3.6"]]
                       :prep-tasks   ["cljs:prod"]
                       :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                       :test-refresh {:refresh-dirs ["src/clj" "test"]}}

             :uberjar {:prep-tasks  ["compile" "cljs"]
                       :omit-source true
                       :aot         :all}}
  :aliases {"cljs"    ["run" "-m" "shadow.cljs.devtools.cli" "release" "app"]
            "uberjar" ["with-profile" "uberjar,server" "uberjar"]})
