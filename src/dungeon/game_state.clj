(ns dungeon.game-state
  (:require [dungeon.location :as location]
            [dungeon.dungeon :as dungeon]
            [dungeon.player :as player]
            [midje.sweet :as t]))

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

(defn tile-at [state location]
  (if (= (player-location state) location)
    :player
    (dungeon/tile-at (:dungeon state) location)))

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

(def valid-move-target? {:floor true
                         :item true})

(defn- can-move-to? [state location]
  (and (in-dungeon? state location)
       (valid-move-target? (tile-at state location))))

(defn- set-player-location [game-state location]
  (if (can-move-to? game-state location)
    (assoc-in game-state [:player :location] location)
    game-state))

(defn has-creature? [game-state location]
  (dungeon/has-monster? (:dungeon game-state) location))

(let [dungeon (read-game-state [".Mim@"])]
  (t/fact
   (has-creature? dungeon [0 0]) => t/falsey
   (has-creature? dungeon [0 1]) => t/truthy
   (has-creature? dungeon [0 2]) => t/falsey
   (has-creature? dungeon [0 3]) => t/truthy
   (has-creature? dungeon [0 4]) => t/falsey))

(defn attack-creature [game-state location damage]
  (update-in game-state
             [:dungeon]
             (fn [dungeon]
               (dungeon/attack-creature dungeon location damage))))

(defn has-item? [game-state location]
  (dungeon/has-item? (:dungeon game-state)
                     location))

(defn pick-item [game-state]
  (update-in game-state
             [:dungeon]
             (fn [dungeon]
               (dungeon/pick-item dungeon
                                  (player-location game-state)))))

(defn move-player [game-state direction]
  (let [player (player game-state)
        delta (direction directions)
        new-location (location/add-delta (player-location game-state) delta)]
    (if (has-creature? game-state new-location)
      (attack-creature game-state new-location (player/damage player))
      (set-player-location game-state new-location))))

(let [dungeon (read-game-state [".i@"])]
  (t/fact
   (state->vec (move-player dungeon :west)) => [".@."]
   (state->vec (move-player (move-player dungeon :west)
                            :west)) => ["@i."]))

(let [dungeon (move-player (read-game-state ["i@"]) :west)]
  (t/fact
   (has-item? dungeon [0 0]) => t/truthy
   (has-item? (pick-item dungeon) [0 0]) => t/falsey
   (has-item? (move-player (pick-item dungeon) :east) [0 0]) => t/falsey))

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
 (tile-at (read-game-state ["..."]) [0 0]) => :floor
 (tile-at (read-game-state [".@."]) [0 1]) => :player)

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
   (state->vec (move-player state :west)) => ["#M@.#"]))
