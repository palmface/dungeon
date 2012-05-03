(ns dungeon.monster
  (:require [midje.sweet :as t]))

(defrecord Monster [hp kind])

(defn make-monster [& {:keys [hp kind]}]
  (->Monster hp kind))

;; (defn dead? [monster]
;;   (<= (:hp monster) 0))

(defn attack-monster [monster damage]
  (let [hp (- (:hp monster) damage)]
    (if (> hp 0)
      (assoc monster :hp hp))))

(t/fact
 (:hp (attack-monster (make-monster :hp 2) 1)) => 1
 (attack-monster (make-monster :hp 1) 1) => nil)
