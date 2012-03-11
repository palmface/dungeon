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
          [:audio {:id "bg-music" :loop "loop"}
           [:source {:src "music.mp3"}]]
          [:canvas {:id "dungeon", :width 640, :height 480}]]))
