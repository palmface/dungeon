(ns dungeon.game-state
  (:use midje.sweet)
  (:require [dungeon.location :as location]
            [dungeon.dungeon :as dungeon]
            [dungeon.player :as player]))

(defrecord GameState [dungeon player])

(defn make-game-state [& {:keys [dungeon player]}]
  (GameState. dungeon player))

(defn read-game-state [dungeon-strings]
  (let [dungeon (dungeon/read-dungeon dungeon-strings)
        player (player/read-player dungeon-strings)]
    (make-game-state :dungeon dungeon
                     :player player)))

(defn player [state]
  (:player state))

(defn player-location [state]
  (player/location (:player state)))

(fact
  (player-location (read-game-state ["..." ".@."])) => [1 1]
  (player-location (read-game-state [".@."])) => [0 1])

(defn height [state]
  (get-in state [:dungeon :height]))

(defn width [state]
  (get-in state [:dungeon :width]))

(defn state->vec [state]
  (let [dungeon (dungeon/dungeon->vec (:dungeon state))]
    (if-let [location (player-location state)]
      (update-in dungeon
                 [(location/row location)]
                 (fn [row-str]
                   (apply str (assoc (vec row-str)
                                (location/col location)
                                \@))))
      dungeon)))

(fact
  (state->vec (read-game-state ["..."])) => ["..."]
  (state->vec (read-game-state ["i@m"])) => ["i@m"]
  (state->vec (read-game-state ["@.."])) => ["@.."])

(defn tile-at [state location]
  (let [content (dungeon/tile-at (:dungeon state) location)]
    (if (= (player-location state) location)
      (assoc content :player true)
      content)))

(fact "tile at returns a map representation of the contents of the
         tile in location. If player is standing in location, [:player
         true] key-value pair is added to the map."
  (tile-at (read-game-state ["..."]) [0 0]) => (dungeon/make-content)
  (:player (tile-at (read-game-state [".@."]) [0 1])) => truthy)

(defn- in-dungeon? [state location]
  (let [h (height state)
        w (width state)
        row (location/row location)
        col (location/col location)]
    (and (>= row 0)
         (>= col 0)
         (< row h)
         (< col w))))

(def directions
  {:north [-1 0]
   :east [0 1]
   :south [1 0]
   :west [0 -1]})

(defn has-monster? [game-state location]
  (dungeon/has-monster? (:dungeon game-state) location))

(let [dungeon (read-game-state [".Mim@"])]
  (fact
    (has-monster? dungeon [0 0]) => falsey
    (has-monster? dungeon [0 1]) => truthy
    (has-monster? dungeon [0 2]) => falsey
    (has-monster? dungeon [0 3]) => truthy
    (has-monster? dungeon [0 4]) => falsey))

(defn attack-monster [game-state location damage]
  (update-in game-state
             [:dungeon]
             dungeon/attack-monster
             location
             damage))

(defn has-item? [game-state location]
  (dungeon/has-item? (:dungeon game-state)
                     location))

(fact
  (has-item? (read-game-state ["i."]) [0 0]) => truthy
  (has-item? (read-game-state ["i."]) [0 1]) => falsey)

(defn pick-item [game-state]
  (let [item-picked (update-in game-state
                               [:dungeon]
                               dungeon/pick-item
                               (player-location game-state))]
    (update-in item-picked
               [:player]
               player/add-item)))

(def walkable-base? {:floor true})

(defn- can-move-to? [state location]
  (and (in-dungeon? state location)
       (walkable-base? (:base (tile-at state location)))
       (not (has-monster? state location))))

(defn- set-player-location [game-state location]
  (if (can-move-to? game-state location)
    (assoc-in game-state [:player :location] location)
    game-state))

(defn move-player [game-state direction]
  (let [player (player game-state)
        delta (direction directions)
        new-location (location/add-delta (player-location game-state) delta)]
    (if (has-monster? game-state new-location)
      (attack-monster game-state new-location (player/damage player))
      (set-player-location game-state new-location))))

(let [dungeon (read-game-state [".i@"])]
  (fact
    (state->vec (move-player dungeon :west)) => [".@."]
    (state->vec (move-player (move-player dungeon :west)
                             :west)) => ["@i."]))

(let [state (read-game-state [".@."])]
  (fact
    (player-location (move-player state :west)) => [0 0]
    (player-location (move-player state :north)) => [0 1]))

(let [dungeon (move-player (read-game-state ["i@"]) :west)]
  (fact
    (has-item? dungeon [0 0]) => truthy
    (has-item? (pick-item dungeon) [0 0]) => falsey))

(let [state (read-game-state ["M@i"])]
  (fact "attack without item does not kill monster with 2 hp"
    (has-monster? (move-player state :west) [0 0]) => truthy)
  (fact "attack with item kills monster with 2 hp"
    (let [state (move-player (pick-item (move-player state :east)) :west)]
      (has-monster? (move-player state :west) [0 0]) => falsey)))

(let [state (read-game-state ["#@..#"])]
  (fact
    (height state) => 1
    (width state) => 5)
  (fact
    (player-location (move-player state :east)) => [0 2])
  (fact
    (player-location (move-player state :west)) => [0 1])
  (fact
    (state->vec state) => ["#@..#"])
  (fact
    (:base (tile-at state [0 2])) => :floor
    (:player (tile-at state [0 1])) => truthy
    (:base (tile-at state [0 0])) => :wall))
