(ns dungeon.tile-content
  (:use midje.sweet))

(defn make-tile-content [& {:keys [base items monster]
                            :or {base :floor}}]
  {:base base :items items :monster monster})

(fact
  (make-tile-content) => {:base :floor :items nil :monster nil}
  (make-tile-content :base :wall) => {:base :wall :items nil :monster nil}
  (make-tile-content :items [...item...]) => {:base :floor
                                              :items [...item...]
                                              :monster nil}
  (make-tile-content :monster ...monster...) => {:base :floor
                                                 :items nil
                                                 :monster ...monster...})

(fact "tile can have a stack of items"
  (:items (make-tile-content)) => nil
  (:items (make-tile-content :items [...item...])) => [...item...]
  (:items (make-tile-content :items [...item... ...item... ...other-item...]))
  => [...item... ...item... ...other-item...])

(defn top-item [content]
  (peek (:items content)))

(fact
  (top-item (make-tile-content)) => nil
  (top-item (make-tile-content :items [.item. .top-item.])) => .top-item.)

(defn remove-top-item [content]
  (let [new-content (when-not (empty? (:items content))
                      (pop (:items content)))]
    (if (empty? new-content)
      (dissoc content :items)
      (assoc content :items new-content))))

(fact "removing from a content with no items does nothing"
  (:items (remove-top-item (make-tile-content))) => (:items (make-tile-content)))

(fact "removing the last item leaves content without items"
  (let [content (make-tile-content :items [...item...])]
    (:items (remove-top-item content))) => falsey)

(fact "removes only one item"
  (let [content (make-tile-content :items [...item... ...other...])]
    (:items (remove-top-item content)) => truthy))

(fact "removes the the top most item, the last in the vector"
  (let [content (make-tile-content :items [...item... ...other...])]
    (:items (remove-top-item content)) => [...item...]))
