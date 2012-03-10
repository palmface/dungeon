(ns dungeon.game-store
  (:use dungeon.game-state))

(def game (atom (read-map (slurp "map.txt"))))

(defn get-map []
  @game)

(defn update-location [action]
  (let [action-sym (keyword action)]
    (println action)
    (swap! game move-player action-sym)))