(ns dungeon.map
  (:require [midje.sweet :as t]))

(defn strings-to-map
  [strings]
  )

(defn read-player-location [rows]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        numbered-rows (map-indexed (fn [i row] [i row])
                                   rows)
        location-if-exists (fn [[row-number row]]
                             (if (contains-player? row)
                               [row-number (player-column row)]))]
    (some location-if-exists numbered-rows)))

(defn read-dungeon [rows]
  (map (fn [row] (.replace row \@ \.)) rows))

(defn read-map [dungeon-string]
  (let [dungeon-string (if (string? dungeon-string)
                         (clojure.string/split dungeon-string #"\n")
                         dungeon-string)
        dungeon (read-dungeon dungeon-string)
        player-location (read-player-location dungeon-string)]
    {:dungeon dungeon
     :player-location player-location}))

(defn player-location [dungeon-state]
  (:player-location dungeon-state))

(defn dungeon [dungeon-state]
  (:dungeon dungeon-state))

(defn dungeon->str [dungeon]
  (clojure.string/join "\n" dungeon))

(t/fact
 (player-location (read-map "...\n.@.")) => [1 1])
(t/fact
 (player-location (read-map [".@."])) => [0 1])
(t/fact
 (player-location (read-map ["@.."])) => [0 0])
(t/fact
 (player-location (read-map ["..." ".@."])) => [1 1])
(t/fact
 (player-location (read-map ["..."])) => nil)
(t/fact
 (dungeon->str (dungeon (read-map [".@."]))) => "...")
(t/fact
 (dungeon->str (dungeon (read-map [".@." "..."]))) => "...\n...")
