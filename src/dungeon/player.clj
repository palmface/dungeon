(ns dungeon.player
  (:use [dungeon.utils :only [some-indexed]])
  (:require [midje.sweet :as t]))

(defrecord Player [location damage])

(defn make-player
  [& {:keys [location damage] :or {damage 1}}]
  (Player. location damage))

(defn read-player [dungeon-strings]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        location-if-exists (fn [row-number row]
                             (if (contains-player? row)
                               [row-number (player-column row)]))
        player-location (some-indexed location-if-exists dungeon-strings)]
    (make-player :location player-location)))

(defn location [player]
  (:location player))

(defn damage [player]
  (:damage player))

(t/fact
 (location (read-player [".@."])) => [0 1]
 (location (read-player ["..." "@.."])) => [1 0]
 (location (read-player ["#@.." "...."])) => [0 1])

(t/fact "player has given damage"
  (damage (make-player :location [0 1] :damage 3)) => 3)

(t/fact "default damage is 1"
  (damage (make-player :location [0 0])) => 1)
