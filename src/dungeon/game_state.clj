(ns dungeon.game-state
  (:use dungeon.player
        dungeon.dungeon
        dungeon.location)
  (:require [midje.sweet :as t]))

(defrecord GameState [dungeon player])

(defn make-game-state [& {:keys [dungeon player]}]
  (GameState. dungeon player))

(defn read-map [dungeon-string]
  (if (string? dungeon-string)
    (read-map (clojure.string/split dungeon-string #"\n"))
    (let [dungeon (make-dungeon :floor (read-dungeon dungeon-string))
          player-location (read-player-location dungeon-string)
          player (make-player :location player-location)]
      (make-game-state :dungeon dungeon :player player))))

(defmethod player-location GameState [state]
  (player-location (get state :player)))

(defmethod dungeon-floor GameState [state]
  (dungeon-floor (:dungeon state)))

(defmethod height GameState [state]
  (height (:dungeon state)))

(defmethod width GameState [state]
  (width (:dungeon state)))

(defn state->vec  [state]
  (let [dungeon (dungeon-floor state)]
    (if-let [location (player-location state)]
      (update-in dungeon
                 [(row location)]
                 (fn [row-str]
                   (apply str (assoc (vec row-str)
                                (col location)
                                \@))))
      dungeon)))

(def tiles {\@ :player
            \. :floor
            \# :wall})

(defn tile-at [state location]
  (let [row (row location)
        col (col location)]
    (let [c (get-in (state->vec state) [row col])]
      (tiles c))))

(defn- in-dungeon? [state location]
  (let [h (height state)
        w (width state)
        row (row location)
        col (col location)]
    (and (>= row 0)
         (>= col 0)
         (< row h)
         (< col w))))

(def directions
  {:north [-1 0]
   :east [0 1]
   :south [1 0]
   :west [0 -1]})

(def movable-tile? {:floor true
                    :player false
                    :wall false})

(defn- movable? [state location]
  (and (in-dungeon? state location)
       (movable-tile? (tile-at state location))))

(defn- set-player-location [game-state location]
  (if (movable? game-state location)
    (assoc-in game-state [:player :location] location)
    game-state))

(defn move-player [game-state direction]
  (let [delta (direction directions)
        new-location (add-delta (player-location game-state) delta)]
    (set-player-location game-state new-location)))

(t/fact
 (player-location (read-map "...\n.@.")) => [1 1]
 (player-location (read-map [".@."])) => [0 1]
 (player-location (read-map ["@.."])) => [0 0]
 (player-location (read-map ["..." ".@."])) => [1 1]
 (player-location (read-map ["..."])) => nil)
(t/fact
 (player-location (move-player (read-map ".@.")
                               :west)) => [0 0]
 (player-location (move-player (read-map ["..." "@.."])
                               :north)) => [0 0]
 (player-location (move-player (read-map "@..")
                               :west)) => [0 0])
(t/fact
 (state->vec (read-map "...")) => ["..."]
 (state->vec (read-map ".@.")) => [".@."]
 (state->vec (read-map "@..")) => ["@.."])
(t/fact
 (tile-at (read-map "...")
          (make-location :row 0 :col 0)) => :floor
 (tile-at (read-map ".@.")
          (make-location :row 0 :col 1)) => :player)

(let [state (read-map "#@..#")]
  (t/fact
   (height state) => 1
   (width state) => 5)
  (t/fact
   (player-location (move-player state :east)) => [0 2])
  (t/fact
   (player-location (move-player state :west)) => [0 1])
  (t/fact
   (state->vec state) => ["#@..#"])
  (t/fact
   (tile-at state [0 2]) => :floor
   (tile-at state [0 1]) => :player
   (tile-at state [0 0]) => :wall))
