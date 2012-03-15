(defproject dungeon "0.1.0"
  :description "roguelike with a web client"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.1"]
                 [hiccup "0.3.7"]
                 [org.clojure/data.json "0.1.2"]
                 [midje "1.3.0"]]
  :plugins [[org.clojars.the-kenny/lein-midje "1.0.9"]
            [lein-ring "0.6.1"]]
  :ring {:handler dungeon.routes/dung}
  :main dungeon.core)
