(ns dungeon.map
  (:require [midje.sweet :as t]))

(defn some-indexed [pred coll]
  (some (fn [[i x]] (pred i x))
        (map-indexed vector coll)))

(defn read-player-location [rows]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        location-if-exists (fn [row-number row]
                             (if (contains-player? row)
                               [row-number (player-column row)]))]
    (some-indexed location-if-exists rows)))

(defn read-dungeon [rows]
  (vec (map (fn [row] (.replace row \@ \.)) rows)))

(defrecord Player [location])

(defn make-player [& {:keys [location]}]
  (Player. location))

(defrecord GameState [dungeon player])

(defn make-game-state [& {:keys [dungeon player]}]
  (GameState. dungeon player))

(defrecord Dungeon [floor])

(defn make-dungeon [& {:keys [floor]}]
  (Dungeon. floor))

(defn read-map [dungeon-string]
  (if (string? dungeon-string)
    (read-map (clojure.string/split dungeon-string #"\n"))
    (let [dungeon (make-dungeon :floor (read-dungeon dungeon-string))
          player-location (read-player-location dungeon-string)
          player (make-player :location player-location)]
      (make-game-state :dungeon dungeon :player player))))

(defmulti player-location type)

(defmethod player-location Player [player]
  (get player :location))

(defmethod player-location GameState [state]
  (player-location (get state :player)))

(defmulti dungeon-floor type)

(defmethod dungeon-floor Dungeon [dungeon]
  (:floor dungeon))

(defmethod dungeon-floor GameState [state]
  (dungeon-floor (:dungeon state)))

(defmulti height type)
(defmulti width type)

(defmethod height Dungeon [dungeon]
  (count (:floor dungeon)))

(defmethod height GameState [state]
  (height (:dungeon state)))

(defmethod width Dungeon [dungeon]
  (count (first (:floor dungeon))))

(defmethod width GameState [state]
  (width (:dungeon state)))

(defn tile-at [dungeon-state [row col]]
  (if (= (player-location dungeon-state) [row col])
    :player
    :floor))

(defn state->vec  [dungeon-state]
  (let [dungeon (dungeon-floor dungeon-state)
        [row col] (player-location dungeon-state)]
    (update-in dungeon
               [row]
               (fn [row-str]
                 (apply str (assoc (vec row-str) col \@))))))

(defn- in-dungeon? [dungeon-state [row col]]
  (let [h (height dungeon-state)
        w (width dungeon-state)]
    (and (>= row 0)
         (>= col 0)
         (< row h)
         (< col w))))

(def directions
  {:north [-1 0]
   :east [0 1]
   :south [1 0]
   :west [0 -1]})

(defn- set-player-location [game-state [row col :as location]]
  (if (in-dungeon? game-state location)
    (assoc-in game-state [:player :location] location)
    game-state))

(defn move-player [game-state direction]
  (let [delta (direction directions)
        new-location (vec (map + (player-location game-state) delta))]
    (set-player-location game-state new-location)))

(t/fact
 (player-location (read-map "...\n.@.")) => [1 1]
 (player-location (read-map [".@."])) => [0 1]
 (player-location (read-map ["@.."])) => [0 0]
 (player-location (read-map ["..." ".@."])) => [1 1]
 (player-location (read-map ["..."])) => nil)
(t/fact
 (height (read-map "...")) => 1
 (height (read-map "...\n...")) => 2
 (width (read-map "...")) => 3
 (width (read-map "...\n...")) => 3)
(t/fact
 (player-location (move-player (read-map ".@.")
                               :west)) => [0 0]
 (player-location (move-player (read-map ["..." "@.."])
                               :north)) => [0 0]
 (player-location (move-player (read-map "@..")
                               :west)) => [0 0])
(t/fact
 (state->vec (read-map ".@.")) => [".@."]
 (state->vec (read-map "@..")) => ["@.."])
(t/fact
 (tile-at (read-map "...") [0 0]) => :floor
 (tile-at (read-map ".@.") [0 1]) => :player)

