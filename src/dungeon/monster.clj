(ns dungeon.monster
  (:require [midje.sweet :as t]))

(defrecord Monster [hp type])

(defn make-monster [& {:keys [hp type]}]
  (Monster. hp type))

(defn monster? [monster]
  (= (type monster) Monster))

(defn dead? [monster]
  (<= (:hp monster) 0))

(defn attack-monster [monster]
  (update-in monster
             [:hp]
             dec))

(t/fact
 (:hp (attack-monster (make-monster :hp 2))) => 1
 (:hp (attack-monster (make-monster :hp 1))) => 0
 (dead? (attack-monster (make-monster :hp 1))) => t/truthy)