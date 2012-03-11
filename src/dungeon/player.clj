(ns dungeon.player
  (:use [midje.sweet :as t]
        [dungeon.utils :only [some-indexed]]))

(defrecord Player [location])

(defn make-player
  [& {:keys [location]}]
  (Player. location))

(defn read-player [dungeon-strings]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        location-if-exists (fn [row-number row]
                             (if (contains-player? row)
                               [row-number (player-column row)]))]
    (make-player :location (some-indexed location-if-exists dungeon-strings))))

(defn player-location [player]
  (get player :location))

(t/fact
 (player-location (read-player [".@."])) => [0 1]
 (player-location (read-player ["..." "@.."])) => [1 0]
 (player-location (read-player ["#@.." "...."])) => [0 1])