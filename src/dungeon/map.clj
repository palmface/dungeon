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

(defn height [dungeon]
  (count dungeon))

(defn width [dungeon]
  (count (first dungeon)))

(defn dungeon->str [dungeon]
  (clojure.string/join "\n" dungeon))

(defn state->vec  [dungeon-state]
  (let [dungeon (dungeon dungeon-state)
        [row col] (player-location dungeon-state)]
    (update-in dungeon
               [row]
               (fn [row-str]
                 (apply str (assoc (vec row-str) col \@))))))

(defn- in-dungeon? [dungeon [row col]]
  (let [h (height dungeon)
        w (width dungeon)]
    (and (>= row 0)
         (>= col 0)
         (< row h)
         (< col w))))

(defn move-player [dungeon-state direction]
  (let [deltas {:north [-1 0]
                :east [0 1]
                :south [1 0]
                :west [0 -1]}
        delta (direction deltas)]
    (update-in dungeon-state
               [:player-location]
               (fn [[row col]]
                 (let [new-location [(+ row (first delta))
                                     (+ col (second delta))]]
                   (if (in-dungeon? (dungeon dungeon-state)
                                    new-location)
                     new-location
                     [row col]))))))

(t/fact
 (player-location (read-map "...\n.@.")) => [1 1]
 (player-location (read-map [".@."])) => [0 1]
 (player-location (read-map ["@.."])) => [0 0]
 (player-location (read-map ["..." ".@."])) => [1 1]
 (player-location (read-map ["..."])) => nil)
(t/fact
 (dungeon->str (dungeon (read-map [".@."]))) => "..."
 (dungeon->str (dungeon (read-map ["..." "..."]))) => "...\n..."
 (dungeon->str (dungeon (read-map [".@." "..."]))) => "...\n...")
(t/fact
 (height (dungeon (read-map "..."))) => 1
 (height (dungeon (read-map "...\n..."))) => 2
 (width (dungeon (read-map "..."))) => 3
 (width (dungeon (read-map "...\n..."))) => 3)
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