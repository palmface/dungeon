(ns dungeon.views
  (:use hiccup.core
        hiccup.page-helpers))

(defn canvas-page []
  (html5 [:head
          [:title "Dungeon game"]
          (include-js "/js/jquery-1.7.1.min.js")
          (include-js "/js/main.js")
          (include-js "/js/crawler.js")
          (include-js "/js/world.js")]
         [:body {:onload "init();"}
          [:canvas {:id "dungeon", :width 640, :height 480}]]))
