(ns dungeon.dungeon
  (:use dungeon.utils
        [midje.sweet :as t])
  (:require [dungeon.monster :as monster]))

(defrecord Dungeon [height width tile-contents])

(def make-content {\# :wall
                   \M (monster/make-monster :hp 1)
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

(defn height [dungeon]
  (:height dungeon))

(defn width [dungeon]
  (:width dungeon))

(defn tile-at [dungeon location]
  ((:tile-contents dungeon) location))

(let [dungeon (read-dungeon ["..."])
      odungeon (read-dungeon ["#M@"])]
  (t/fact
   (tile-at dungeon [0 0]) => :floor
   (tile-at dungeon [0 1]) => :floor
   (tile-at dungeon [0 2]) => :floor)
  (t/fact
   (tile-at odungeon [0 0]) => :wall
   (tile-at odungeon [0 1]) => :monster
   (tile-at odungeon [0 2]) => :floor))

(def content-char {:floor \.
                   :wall \#
                   :monster \M})

(defn dungeon->vec [dungeon]
  (vec
   (for [row (range (height dungeon))]
     (apply str
            (for [col (range (width dungeon))]
              (content-char (tile-at dungeon [row col])))))))

(t/fact
 (dungeon->vec (read-dungeon [".#M." ".#MM"])) => [".#M." ".#MM"]
 (dungeon->vec (read-dungeon [".#@M."])) => [".#.M."])

(let [dungeon (read-dungeon [".#M.@"])]
  (t/fact
   (tile-at dungeon [0 0]) => :floor
   (tile-at dungeon [0 1]) => :wall
   (tile-at dungeon [0 2]) => :monster
   (tile-at dungeon [0 4]) => :floor))

(defn dungeon-floor [dungeon]
  (:floor dungeon))

(let [dungeon13 (read-dungeon ["..."])
      dungeon23 (read-dungeon ["..." "..."])]
  (t/fact
   (height dungeon13) => 1
   (height dungeon23) => 2
   (width dungeon13) => 3
   (width dungeon23) => 3))

(defn has-creature? [dungeon loc]
  (= ((:tile-contents dungeon) loc) :monster))

(let [dungeon (read-dungeon [".M." "MM."])]
  (t/fact
   (has-creature? dungeon [0 0]) => falsey
   (has-creature? dungeon [0 1]) => truthy
   (has-creature? dungeon [1 1]) => truthy))

(defn remove-creature [dungeon loc]
  (update-in dungeon
            [:tile-contents loc]
            (fn [old-content]
              (if (= old-content :monster)
                :floor
                old-content))))

(let [dungeon (read-dungeon ["M.#"])]
  (t/fact
   (tile-at (remove-creature dungeon [0 0]) [0 0]) => :floor
   (tile-at (remove-creature dungeon [0 2]) [0 2]) => :wall
   (tile-at (remove-creature dungeon [0 1]) [0 1]) => :floor))