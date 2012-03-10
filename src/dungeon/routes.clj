(ns dungeon.routes
  (:use compojure.core
        dungeon.views
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes player-routes
  (route/not-found "duh"))

(defroutes dungeon-routes
  (context "/api" [] player-routes)
  (GET "/" [] (canvas-page))
  (route/resources "/")
  (route/not-found "Page not found"))


(def dung
  (->
   (handler/site dungeon-routes)
   (wrap-base-url)))