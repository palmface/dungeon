(ns dungeon.utils
  (:require [midje.sweet :as t]))

(defn some-indexed [pred coll]
  (some (fn [[i x]] (pred i x))
        (map-indexed vector coll)))

(defn with-coordinates [dungeon-string]
  (apply concat
         (map-indexed (fn [row row-str]
                        (map-indexed (fn [col char]
                                       [[row col] char])
                                     row-str))
                      dungeon-string)))

(t/fact
 (with-coordinates [".M."]) => [[[0 0] \.] [[0 1] \M] [[0 2] \.]])

(t/fact
 (some-indexed (fn [i x]
                 (if (= i 1)
                   x))
               ['a 'b 'c]) => 'b
 (some-indexed (fn [i x]
                 (if (= x 'c)
                   i))
               ['a 'b 'c]) => 2
 (some-indexed (fn [i x]
                 (if (= i x)
                   true))
               ['a 'b 'c]) => nil)