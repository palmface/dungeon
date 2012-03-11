(ns dungeon.game-store
  (:require [dungeon.game-state :as gs]))

(defn read-map-file []
  (vec (clojure.string/split-lines (slurp "map.txt"))))

(def game (atom (gs/read-game-state (read-map-file))))

(defn get-map []
  @game)

(defn update-location [action]
  (let [action-sym (keyword action)]
    (swap! game gs/move-player action-sym)))