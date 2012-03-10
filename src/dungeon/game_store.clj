(ns dungeon.game-store
  (:use dungeon.map))

(def game (atom (read-map ".@.")))

(defn get-map []
  @game)

(defn update-location [action]
  (swap! game (move-player @game action)))