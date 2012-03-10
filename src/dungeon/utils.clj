(ns dungeon.utils)

(defn some-indexed [pred coll]
  (some (fn [[i x]] (pred i x))
        (map-indexed vector coll)))