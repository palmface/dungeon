(ns dungeon.routes
  (:use compojure.core
        dungeon.views
        [dungeon.map :as map]
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]))

(defn blob [game-state]
  (for [column (range (width game-state))
        row (range (height game-state))]
    (let [tile (tile-at game-state [column row])]
      {:x column :y row :type tile})))


(defn map->json [map-state]
  (let [width (map/width map-state)
        height (map/height map-state)
        contents (blob map-state)]
    (json/json-str {:width width :height height :contents contents})))


(defroutes player-routes
  (GET "/" [] (map->json (map/read-map "..@.."))) ; Get current map information
  (PUT "/" [] ()) ; Move player to given location
  (route/not-found "duh"))

(defroutes dungeon-routes
  (context "/api" [] player-routes)
  (GET "/" [] (canvas-page))
  (PUT "/dummy.json" [] "{}")
  (route/resources "/")
  (route/not-found "Page not found"))


(def dung
  (->
   (handler/site dungeon-routes)
   (wrap-base-url)))
