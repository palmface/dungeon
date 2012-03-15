(ns dungeon.tile-content
  (:use midje.sweet))

(defn make-tile-content [& {:keys [base items monster]
                            :or {base :floor}}]
  (let [content {:base base}
        content (if (nil? items)
                  content
                  (assoc content :items items))
        content (if (nil? monster)
                  content
                  (assoc content :monster monster))]
    content))

(fact
  (make-tile-content) => {:base :floor}
  (make-tile-content :base :wall) => {:base :wall}
  (make-tile-content :items [...item...]) => {:base :floor
                                              :items [...item...]}
  (make-tile-content :monster ...monster...) => {:base :floor
                                                 :monster ...monster...})

(defn base [content]
  (:base content))

(defn has-items? [content]
  (contains? content :items))

(defn items [content]
  (:items content))

(fact "tile can have a multiset of items"
  (items (make-tile-content)) => nil
  (items (make-tile-content :items [...item...])) => [...item...]
  (items (make-tile-content :items [...item... ...item... ...other-item...]))
  => [...item... ...item... ...other-item...])

(defn remove-top-item [content]
  (if (has-items? content)
    (let [new-items (pop (:items content))]
      (if (empty? new-items)
        (dissoc content :items)
        (assoc content :items new-items)))
    content))

(fact "removing from a content with no items does nothing"
  (remove-top-item (make-tile-content)) => (make-tile-content))

(fact "removing the last item leaves content without items"
  (let [content (make-tile-content :items [...item...])]
    (has-items? (remove-top-item content))) => falsey)

(fact "removes only one item"
  (let [content (make-tile-content :items [...item... ...other...])]
    (has-items? (remove-top-item content)) => truthy))

(fact "removes the the top most item, the last in the vector"
  (let [content (make-tile-content :items [...item... ...other...])]
    (items (remove-top-item content)) => [...item...]))

(defn has-monster? [content]
  (contains? content :monster))

(fact "tile can have a monster"
  (has-monster? (make-tile-content)) => falsey
  (has-monster? (make-tile-content :monster ...monster...)) => truthy)

(defn monster [content]
  (:monster content))

(defn set-monster [monster content]
  (assoc content :monster monster))

(fact "monster can be set"
  (let [content (make-tile-content :monster ...monster...)]
    (monster (set-monster ...other-monster... content)) => ...other-monster...))

(defn remove-monster [content]
  (dissoc content :monster))

(fact "monster can be removed"
  (let [content (make-tile-content :monster ...monster...)]
    (has-monster? (remove-monster content))) => falsey)