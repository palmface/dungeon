(ns dungeon.dungeon
  (:use [dungeon.utils :only [with-coordinates]]
        midje.sweet)
  (:require [dungeon.monster :as monster]))

(unfinished )

(defrecord Dungeon [height width tile-contents])

(defn make-content [& {:keys [base items monster]
                       :or {base :floor
                            items []
                            monster nil}}]
  {:base base
   :items items
   :monster monster})

(fact
  (make-content) => {:base :floor
                     :items []
                     :monster nil}
  (make-content :base :wall) => {:base :wall
                                 :items []
                                 :monster nil})

(def contents {\m (make-content
                   :monster (monster/make-monster :hp 1 :type :monster))
               \M (make-content
                   :monster (monster/make-monster :hp 2 :type :Monster))
               \i (make-content :items [:item])
               \# (make-content :base :wall)
               \. (make-content)})

(defn make-dungeon [& {:keys [height width tile-contents]}]
  (Dungeon. height width tile-contents))

(defn read-dungeon [dungeon-strings]
  (let [height (count dungeon-strings)
        width (count (first dungeon-strings))
        tile-contents (reduce
                       (fn [tile-contents [location content]]
                         (assoc tile-contents
                           location
                           (contents content (make-content))))
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
  (tile-at (read-dungeon [".i"]) [0 0]) => (make-content)
  (tile-at (read-dungeon [".i"]) [0 1]) => (make-content :items [:item]))

(defn has-monster? [dungeon location]
  (monster/monster? (:monster (tile-at dungeon location))))

(let [dungeon (read-dungeon [".M." "MM."])]
  (fact
    (has-monster? dungeon [0 0]) => falsey
    (has-monster? dungeon [0 1]) => truthy
    (has-monster? dungeon [1 1]) => truthy))

(defn items [content]
  (:items content))

(fact "tile can have a multiset of items"
  (items (make-content :items [])) => []
  (items (make-content :items [.item.])) => [.item.]
  (items (make-content :items [.item. .item. .other-item.]))
  => [.item. .item. .other-item.])

(defn has-item? [dungeon location]
  (not (empty? (items (tile-at dungeon location)))))

(fact
  (has-item? (read-dungeon [".i."]) [0 1]) => truthy
  (has-item? (read-dungeon [".i."]) [0 0]) => falsey
  (has-item? (read-dungeon [".M."]) [0 1]) => falsey)

(defn pick-item [dungeon location]
  (if (has-item? dungeon location)
    (update-in dungeon
               [:tile-contents location :items]
               pop)
    dungeon))

(let [dungeon (read-dungeon ["mi." "ii."])]
  (fact "pick-item picks items"
    (has-item? (pick-item dungeon [0 0]) [0 0]) => falsey
    (has-item? (pick-item dungeon [0 1]) [0 1]) => falsey
    (has-item? (pick-item dungeon [1 1]) [1 0]) => truthy)
  (fact "pick-item does not pick anything else"
    (has-monster? (pick-item dungeon [0 0]) [0 0]) => truthy))

(defn attack-monster [dungeon location damage]
  (if (has-monster? dungeon location)
    (update-in dungeon
               [:tile-contents location :monster]
               (fn [monster]
                 (let [hit-monster (monster/attack-monster monster damage)]
                   (if (not (monster/dead? hit-monster))
                     hit-monster))))
    dungeon))

(fact
  (has-monster? (attack-monster (read-dungeon ["M."]) [0 0] 1) [0 0])
  => truthy
  (has-monster? (attack-monster (read-dungeon ["m."]) [0 0] 1) [0 0])
  => falsey
  (has-monster? (attack-monster (read-dungeon ["M."]) [0 0] 2) [0 0])
  = falsey)

(defn as-char [dungeon location]
  (cond (has-monster? dungeon location)
        ({:Monster \M
          :monster \m} (get-in (tile-at dungeon location)
                               [:monster :type]))
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
