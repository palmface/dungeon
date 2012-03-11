(ns dungeon.monster
  (:use [midje.sweet :as t]))

(defrecord Monster [hp])

(defn make-monster [& {:keys [hp]}]
  (Monster. hp))

(defn monster? [monster]
  (= (type monster) Monster))

(defn attack-monster [monster]
  (update-in monster
             [:hp]
             dec))

(t/fact
 (:hp (attack-monster (make-monster :hp 2))) => 1
 (:hp (attack-monster (make-monster :hp 1))) => 0)