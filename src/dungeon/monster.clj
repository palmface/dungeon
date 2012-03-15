(ns dungeon.monster
  (:require [midje.sweet :as t]))

(defrecord Monster [hp kind])

(defn make-monster [& {:keys [hp kind]}]
  (Monster. hp kind))

(defn monster? [monster]
  (= (type monster) Monster))

(defn hp [monster]
  (:hp monster))

(defn set-hp [monster new-hp]
  (assoc monster :hp new-hp))

(defn kind [monster]
  (:kind monster))

(defn dead? [monster]
  (<= (hp monster) 0))

(defn attack-monster [monster damage]
  (let [current-hp (hp monster)]
    (set-hp monster
            (- current-hp damage))))

(t/fact
 (hp (attack-monster (make-monster :hp 2) 1)) => 1
 (hp (attack-monster (make-monster :hp 1) 1)) => 0
 (hp (attack-monster (make-monster :hp 2) 2)) => 0
 (dead? (attack-monster (make-monster :hp 1) 1)) => t/truthy)
