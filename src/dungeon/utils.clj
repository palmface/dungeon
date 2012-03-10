(ns dungeon.utils
  (:use [midje.sweet :as t]))

(defn some-indexed [pred coll]
  (some (fn [[i x]] (pred i x))
        (map-indexed vector coll)))

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