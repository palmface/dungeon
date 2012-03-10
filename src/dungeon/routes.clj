(ns dungeon.routes
  (:use compojure.core
        dungeon.views
        dungeon.game-store
        dungeon.game-state
        dungeon.dungeon
        ring.middleware.json-params
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]))

(defn map-tiles [game-state]
  (for [column (range (width game-state))
        row (range (height game-state))]
    (let [tile (tile-at game-state [row column])]
      {:x column :y row :type tile})))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/json-str data)})

(defn map->json [map-state]
  (let [width (width map-state)
        height (height map-state)
        contents (map-tiles map-state)]
    (json-response {:width width :height height :contents contents})))


(defroutes player-routes
  (GET "/" [] (map->json (get-map))) ; Get current map information
  (PUT "/" [action] (map->json (update-location action))) ; Move player to given location
  (route/not-found "duh"))

(defroutes dungeon-routes
  (context "/api" [] player-routes)
  (GET "/" [] (canvas-page))
  (route/resources "/")
  (route/not-found "Page not found"))


(def dung
  (-> dungeon-routes
      wrap-json-params))