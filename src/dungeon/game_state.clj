(ns dungeon.game-state
  (:use dungeon.player
        dungeon.location)
  (:require [dungeon.dungeon :as dungeon]
            [midje.sweet :as t]))

(defrecord GameState [dungeon player])

(defn make-game-state [& {:keys [dungeon player]}]
  (GameState. dungeon player))

(defn read-game-state [dungeon-strings]
  (let [dungeon (dungeon/read-dungeon dungeon-strings)
        player (read-player dungeon-strings)]
    (make-game-state :dungeon dungeon
                     :player player)))

(defmethod player-location GameState [state]
  (player-location (get state :player)))

(defn height [state]
  (dungeon/height (:dungeon state)))

(defn width [state]
  (dungeon/width (:dungeon state)))

(defn state->vec [state]
  (let [dungeon (dungeon/dungeon->vec (:dungeon state))]
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
  (if (= (player-location state) location)
    :player
    (dungeon/tile-at (:dungeon state) location)))

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

(defn- can-move-to? [state location]
  (and (in-dungeon? state location)
       (movable-tile? (tile-at state location))))

(defn- set-player-location [game-state location]
  (if (can-move-to? game-state location)
    (assoc-in game-state [:player :location] location)
    game-state))

(defn has-creature? [game-state location]
  (dungeon/has-creature? (:dungeon game-state) location))

(defn kill-creature [game-state location]
  (update-in game-state
             [:dungeon]
             (fn [dungeon]
               (dungeon/remove-creature dungeon location))))

(defn move-player [game-state direction]
  (let [delta (direction directions)
        new-location (add-delta (player-location game-state) delta)]
    (if (has-creature? game-state new-location)
      (kill-creature game-state new-location)
      (set-player-location game-state new-location))))

(t/fact
 (player-location (read-game-state ["..." ".@."])) => [1 1]
 (player-location (read-game-state [".@."])) => [0 1]
 (player-location (read-game-state ["@.."])) => [0 0]
 (player-location (read-game-state ["..." ".@."])) => [1 1]
 (player-location (read-game-state ["..."])) => nil)
(t/fact
 (player-location (move-player (read-game-state [".@."])
                               :west)) => [0 0]
 (player-location (move-player (read-game-state ["..." "@.."])
                               :north)) => [0 0]
                               (player-location (move-player (read-game-state ["@.."])
                               :west)) => [0 0])
(t/fact
 (state->vec (read-game-state ["..."])) => ["..."]
 (state->vec (read-game-state [".@."])) => [".@."]
 (state->vec (read-game-state ["@.."])) => ["@.."])
(t/fact
 (tile-at (read-game-state ["..."])
          (make-location :row 0 :col 0)) => :floor
 (tile-at (read-game-state [".@."])
          (make-location :row 0 :col 1)) => :player)

(let [state (read-game-state ["#@..#"])]
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

(let [state (read-game-state ["#M@.#"])]
  (t/fact
   (state->vec state) => ["#M@.#"])
  (t/fact
   (state->vec (kill-creature state [0 1])) => ["#.@.#"])
  (t/fact
   (state->vec (move-player state :west)) => ["#.@.#"]))
