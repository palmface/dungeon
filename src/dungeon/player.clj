(ns dungeon.player
  (:use [midje.sweet :as t]
        [dungeon.utils :only [some-indexed]]))

(defrecord Player [location])

(defn read-player-location [rows]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        location-if-exists (fn [row-number row]
                             (if (contains-player? row)
                               [row-number (player-column row)]))]
    (some-indexed location-if-exists rows)))

(defn make-player
  [& {:keys [location]}]
  (Player. location))

(defmulti player-location type)

(defmethod player-location Player [player]
  (get player :location))

(t/fact
 (read-player-location ["..."]) => nil
 (read-player-location [".@."]) => [0 1]
 (read-player-location ["..." "@.."]) => [1 0]
 (read-player-location ["#@.." "...."]) => [0 1])