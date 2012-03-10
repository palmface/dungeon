(ns dungeon.dungeon
  (:use [midje.sweet :as t]))

(defrecord Dungeon [floor])

(defn read-dungeon [rows]
  (vec (map (fn [row] (.replace row \@ \.)) rows)))

(defn make-dungeon [& {:keys [floor]}]
  (Dungeon. floor))

(defmulti dungeon-floor type)

(defmethod dungeon-floor Dungeon [dungeon]
  (:floor dungeon))

(defmulti height type)

(defmethod height Dungeon [dungeon]
  (count (:floor dungeon)))

(defmulti width type)

(defmethod width Dungeon [dungeon]
  (count (first (:floor dungeon))))

(let [dungeon13 (make-dungeon :floor (read-dungeon ["..."]))
      dungeon23 (make-dungeon :floor (read-dungeon ["..." "..."]))]
  (t/fact
   (height dungeon13) => 1
   (height dungeon23) => 2
   (width dungeon13) => 3
   (width dungeon23) => 3))