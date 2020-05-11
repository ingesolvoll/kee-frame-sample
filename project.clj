(defproject kee-frame-sample "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.597"]
                 [kee-frame "0.4.1-SNAPSHOT"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6" :exclusions [reagent]]
                 [day8.re-frame/http-fx "0.1.5"]
                 [cljs-react-material-ui "0.2.48"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/cljs"]

  :test-paths ["test"]

  :uberjar-name "kee-frame-sample.jar"

  :main kee-frame-sample.server

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:server  {:source-paths ["src/clj"]
                       :dependencies [[ring "1.7.1"]
                                      [ring/ring-defaults "0.2.1"]
                                      [compojure "1.6.0"]
                                      [environ "1.0.0"]
                                      [ring/ring-jetty-adapter "1.7.1"]]}
             :test    {:source-paths ["test"]
                       :dependencies [[etaoin "0.3.6"]]
                       :prep-tasks   ["cljs:prod"]
                       :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                       :test-refresh {:refresh-dirs ["src/clj" "test"]}}

             :dev     {:dependencies [[com.bhauman/figwheel-main "0.2.4"]
                                      [day8.re-frame/re-frame-10x "0.6.4"]]}

             :uberjar {:prep-tasks  ["compile" "cljs:prod"]
                       :omit-source true
                       :aot         :all}}
  :aliases {"integration-test" ["with-profile" "server" "test"]
            "figwheel"         ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "cljs:prod"        ["run" "-m" "cljs.main" "-co" "prod.cljs.edn" "-c"]
            "uberjar"          ["with-profile" "uberjar,server" "uberjar"]})