(defproject clordle "0.0.1"
  :description "Wordle clone in written in Clojure"
  :license {:name "MIT Public License"
            :url "https://opensource.org/license/mit"}
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [hiccup "2.0.0-RC5"]
                 [ring/ring-core "1.14.2"]
                 [ring/ring-jetty-adapter "1.14.2"]
                 [ring/ring-defaults "0.6.0"]
                 [compojure "1.7.1"]]
  :plugins [[lein-tar "1.1.2"]]
  :target-path "dist"
  :aot [clordle.core]
  :main clordle.core)
