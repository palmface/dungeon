(ns dungeon.dungeon
  (:use [dungeon.utils :only [with-coordinates]]
        midje.sweet)
  (:require [dungeon.monster :as monster]
            [dungeon.tile-content :as content]))

(defrecord Dungeon [height width tile-contents])

(def contents {\m (content/make-tile-content
                   :monster (monster/make-monster :hp 1 :kind :monster))
               \M (content/make-tile-content
                   :monster (monster/make-monster :hp 2 :kind :Monster))
               \i (content/make-tile-content :items [:item])
               \# (content/make-tile-content :base :wall)
               \. (content/make-tile-content)})

(defn make-dungeon [& {:keys [height width tile-contents]}]
  (->Dungeon height width tile-contents))

(defn read-dungeon [dungeon-strings]
  (let [height (count dungeon-strings)
        width (count (first dungeon-strings))
        tile-contents (reduce
                       (fn [tile-contents [location content]]
                         (assoc tile-contents
                           location
                           (contents content (content/make-tile-content))))
                       {}
                       (with-coordinates dungeon-strings))]
    (make-dungeon :height height
                  :width width
                  :tile-contents tile-contents)))

(let [dungeon13 (read-dungeon ["..."])
      dungeon23 (read-dungeon ["..." "..."])]
  (fact
    (:height dungeon13) => 1
    (:height dungeon23) => 2
    (:width dungeon13) => 3
    (:width dungeon23) => 3))

(defn tile-at [dungeon location]
  (get-in dungeon [:tile-contents location]))

(fact
  (tile-at (read-dungeon [".i"]) [0 0]) => (content/make-tile-content)
  (tile-at (read-dungeon [".i"]) [0 1]) => (content/make-tile-content :items [:item]))

(defn has-monster? [dungeon location]
  (:monster (tile-at dungeon location)))

(let [dungeon (read-dungeon [".M." "MM."])]
  (fact
    (has-monster? dungeon [0 0]) => falsey
    (has-monster? dungeon [0 1]) => truthy
    (has-monster? dungeon [1 1]) => truthy))

(defn has-item? [dungeon location]
  (:items (tile-at dungeon location)))

(fact
  (has-item? (read-dungeon [".i."]) [0 1]) => truthy
  (has-item? (read-dungeon [".i."]) [0 0]) => falsey
  (has-item? (read-dungeon [".M."]) [0 1]) => falsey)

(defn top-item [dungeon location]
  (content/top-item (get-in dungeon
                            [:tile-contents
                             location])))

(fact
  (top-item (read-dungeon ["."]) [0 0]) => nil
  (top-item (read-dungeon ["i"]) [0 0]) =not=> nil)

(defn remove-item [dungeon location]
  (update-in dungeon
             [:tile-contents location]
             content/remove-top-item))

(let [dungeon (read-dungeon ["mi." "ii."])]
  (fact "remove-item picks items"
    (has-item? (remove-item dungeon [0 0]) [0 0]) => falsey
    (has-item? (remove-item dungeon [0 1]) [0 1]) => falsey
    (has-item? (remove-item dungeon [1 1]) [1 0]) => truthy)
  (fact "remove-item does not pick anything else"
    (has-monster? (remove-item dungeon [0 0]) [0 0]) => truthy))

(defn attack-monster [dungeon location damage]
  (update-in dungeon
             [:tile-contents location :monster]
             (fn [monster]
               (when monster
                 (monster/attack-monster monster damage)))))

(fact
  (has-monster? (attack-monster (read-dungeon ["M."]) [0 0] 1) [0 0])
  => truthy
  (has-monster? (attack-monster (read-dungeon ["m."]) [0 0] 1) [0 0])
  => falsey
  (has-monster? (attack-monster (read-dungeon ["M."]) [0 0] 2) [0 0])
  = falsey)

(defn as-char [dungeon location]
  (cond (has-monster? dungeon location)
        (let [tile (tile-at dungeon location)]
          ({:Monster \M
            :monster \m} (:kind (:monster tile))))
        (has-item? dungeon location) \i
        :else
        ({:floor \.
          :wall \#} (:base (tile-at dungeon location)))))

(fact
  (as-char (read-dungeon ["."]) [0 0]) => \.
  (as-char (read-dungeon ["i"]) [0 0]) => \i
  (as-char (read-dungeon ["m"]) [0 0]) => \m
  (as-char (read-dungeon ["M"]) [0 0]) => \M)

(defn dungeon->vec [dungeon]
  (vec
   (for [row (range (:height dungeon))]
     (apply str
            (for [col (range (:width dungeon))]
              (as-char dungeon [row col]))))))

(fact
  (dungeon->vec (read-dungeon [".#Mi" ".#MM"])) => [".#Mi" ".#MM"]
  (dungeon->vec (read-dungeon [".#@M."])) => [".#.M."])
