(ns dungeon.dungeon
  (:use [dungeon.utils :only [with-coordinates]])
  (:require [dungeon.monster :as monster]
            [midje.sweet :as t]))

(defrecord Dungeon [height width tile-contents])

(def make-content {\# :wall
                   \m (monster/make-monster :hp 1 :type :monster)
                   \M (monster/make-monster :hp 2 :type :Monster)
                   \. :floor})

(defn make-dungeon [& {:keys [height width tile-contents]}]
  (Dungeon. height width tile-contents))

(defn read-dungeon [dungeon-strings]
  (let [height (count dungeon-strings)
        width (count (first dungeon-strings))
        tile-contents (reduce
                       (fn [tile-contents [location content]]
                         (assoc tile-contents
                           location
                           (make-content content :floor)))
                       {}
                       (with-coordinates dungeon-strings))]
    (make-dungeon :height height
                  :width width
                  :tile-contents tile-contents)))

(defn has-creature? [dungeon loc]
  (monster/monster? ((:tile-contents dungeon) loc)))

(let [dungeon (read-dungeon [".M." "MM."])]
  (t/fact
   (has-creature? dungeon [0 0]) => t/falsey
   (has-creature? dungeon [0 1]) => t/truthy
   (has-creature? dungeon [1 1]) => t/truthy))

(defn tile-at [dungeon location]
  (if (has-creature? dungeon location)
    (:type ((:tile-contents dungeon) location))
    ((:tile-contents dungeon) location)))

(let [dungeon (read-dungeon ["..."])
      odungeon (read-dungeon ["#M@"])]
  (t/fact
   (tile-at dungeon [0 0]) => :floor
   (tile-at dungeon [0 1]) => :floor
   (tile-at dungeon [0 2]) => :floor)
  (t/fact
   (tile-at odungeon [0 0]) => :wall
   (tile-at odungeon [0 1]) => :Monster
   (tile-at odungeon [0 2]) => :floor))

(def content-char {:floor \.
                   :wall \#
                   :Monster \M
                   :monster \m})

(defn dungeon->vec [dungeon]
  (vec
   (for [row (range (:height dungeon))]
     (apply str
            (for [col (range (:width dungeon))]
              (content-char (tile-at dungeon [row col])))))))

(t/fact
 (dungeon->vec (read-dungeon [".#M." ".#MM"])) => [".#M." ".#MM"]
 (dungeon->vec (read-dungeon [".#@M."])) => [".#.M."])

(let [dungeon (read-dungeon [".#M.@"])]
  (t/fact
   (tile-at dungeon [0 0]) => :floor
   (tile-at dungeon [0 1]) => :wall
   (tile-at dungeon [0 2]) => :Monster
   (tile-at dungeon [0 4]) => :floor))

(defn dungeon-floor [dungeon]
  (:floor dungeon))

(let [dungeon13 (read-dungeon ["..."])
      dungeon23 (read-dungeon ["..." "..."])]
  (t/fact
   (:height dungeon13) => 1
   (:height dungeon23) => 2
   (:width dungeon13) => 3
   (:width dungeon23) => 3))

(defn remove-creature [dungeon loc]
  (update-in dungeon
            [:tile-contents loc]
            (fn [old-content]
              (if (= old-content :monster)
                :floor
                old-content))))

(defn attack-creature [dungeon location]
  (update-in dungeon
             [:tile-contents location]
             (fn [old-content]
               (if (monster/monster? old-content)
                 (let [monster (monster/attack-monster old-content)]
                   (if (> (:hp monster) 0)
                     monster
                     :floor))
                 old-content))))

(let [dungeon (read-dungeon ["M.#"])
      odungeon (read-dungeon ["m.#"])]
  (t/fact
   (tile-at (attack-creature dungeon [0 0]) [0 0]) => :Monster
   (tile-at (attack-creature odungeon [0 0]) [0 0]) => :floor))
