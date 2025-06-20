(defproject clordle "0.0.1"
  :description "Wordle clone in written in Clojure"
  :license {:name "MIT Public License"
            :url "https://opensource.org/license/mit"}
  :dependencies [[org.clojure/clojure "1.12.1"]

                 ;; HTML Templates 
                 [hiccup "2.0.0-RC5"]

                 ;; Web server and routing
                 [ring/ring-core "1.14.2"]
                 [ring/ring-jetty-adapter "1.14.2"]
                 [ring/ring-defaults "0.6.0"]
                 [compojure "1.7.1"]

                 ;; Database
                 [com.github.seancorfield/next.jdbc "1.3.1048"]
                 [com.h2database/h2 "2.3.232"]

                 ;; Task scheduling
                 [jarohen/chime "0.3.3"]]
  :plugins [[lein-tar "1.1.2"]]
  :target-path "dist"
  :aot [clordle.core]
  :main clordle.core)
