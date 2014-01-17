(defproject cheap-life "0.1.0-SNAPSHOT"
  :description "Demonstrates the use of the World Bank API"
  :url "https://github.com/clojurebridge/cheap-life"
  :license {:name "Creative Commons Attribution License"
            :url "http://creativecommons.org/licenses/by/3.0/"}
  :plugins [[lein-ring "0.8.10"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.7"]
                 [cheshire "5.2.0"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]]
  :main ^:skip-aot cheap-life.core
  :ring {:handler cheap-life.core/main-routes}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
