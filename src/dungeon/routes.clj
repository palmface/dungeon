(ns dungeon.routes
  (:use compojure.core
        dungeon.views
        dungeon.game-store
        dungeon.location
        ring.middleware.json-params
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]
            [dungeon.game-state :as gs]))

(defn map-tiles [game-state]
  (for [column (range (gs/width game-state))
        row (range (gs/height game-state))]
    (let [tile (gs/tile-at game-state (make-location :row row :col column))]
      {:x column :y row :type tile})))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/json-str data)})

(defn map->json [map-state]
  (let [width (gs/width map-state)
        height (gs/height map-state)
        contents (map-tiles map-state)]
    (json-response {:width width :height height :contents contents})))


(defn simple-logging-middleware [app]
  (fn [req]
    (println "\n")
    (println req)
    (app req)))


(defroutes player-routes
  (GET "/" [] (map->json (get-map))) ; Get current map information
  (GET "/west" [action] (map->json (update-location "west")))
  (GET "/east" [action] (map->json (update-location "east")))
  (GET "/south" [action] (map->json (update-location "south")))
  (GET "/north" [action] (map->json (update-location "north")))
  (route/not-found "duh"))

(defroutes dungeon-routes
  (context "/api" [] player-routes)
  (GET "/" [] (canvas-page))
  (route/resources "/")
  (route/not-found "Page not found"))


(def dung
  (-> dungeon-routes
      simple-logging-middleware
      wrap-json-params))
