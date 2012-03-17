(ns dungeon.player
  (:use [dungeon.utils :only [some-indexed]]
        midje.sweet))

(defrecord Player [location damage inventory])

(defn make-player
  [& {:keys [location damage inventory] :or {damage 1
                                             inventory []}}]
  (Player. location damage inventory))

(defn read-player [dungeon-strings]
  (let [player-column (fn [row] (.indexOf row "@"))
        contains-player? (fn [row] (>= (player-column row) 0))
        location-if-exists (fn [row-number row]
                             (if (contains-player? row)
                               [row-number (player-column row)]))
        player-location (some-indexed location-if-exists dungeon-strings)]
    (make-player :location player-location)))

(defn location [player]
  (:location player))

(fact
  (location (read-player [".@."])) => [0 1]
  (location (read-player ["..." "@.."])) => [1 0]
  (location (read-player ["#@.." "...."])) => [0 1])

(defn damage [player]
  (:damage player))

(fact "player has given damage"
  (damage (make-player :location [0 1] :damage 3)) => 3)

(fact "default damage is 1"
  (damage (make-player :location [0 0])) => 1)

(defn add-item [player item]
  (-> player
      (update-in [:inventory]
                 conj item)
      (update-in [:damage]
                 inc)))

(fact
  (inventory (add-item (make-player :location [.x. .y.]) .item.)) => [.item.]
  (inventory (add-item (make-player :location [.x. .y.]
                                    :inventory [.item.])
                       .other-item.))
  => [.item. .other-item.])

(fact "adding item increases damage by 1"
  (damage (add-item (make-player :location [0 0]) .item.)) => 2)

(defn inventory [player]
  (:inventory player))

(fact
  (inventory (make-player :location [.x. .y.])) => empty?
  (inventory (make-player :location [.x. .y.] :inventory [.item.])) =not=> empty?
  (inventory (make-player :location [.x. .y.] :inventory [.item.])) => [.item.])