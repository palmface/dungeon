(defproject dungeon "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring-json-params "0.1.0"]
                 [compojure "1.0.1"]
                 [hiccup "0.3.7"]
                 [org.clojure/data.json "0.1.2"]]
  :dev-dependencies [[midje "1.3.1"]
                     [lein-midje "[1.0.0,)"]
                     [lein-ring "0.5.4"]]
  :ring {:handler dungeon.routes/dung})

