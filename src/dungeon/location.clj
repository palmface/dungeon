(ns dungeon.location
  (:use [midje.sweet :as t]))

(defn make-location [& {:keys [row col]}]
  [row col])

(defn row [[row _]]
  row)

(defn col [[_ col]]
  col)

(defn add-delta [[row col] [row-delta col-delta]]
  (make-location :row (+ row row-delta)
                 :col (+ col col-delta)))

(let [loc (make-location :row 0 :col 1)]
  (t/fact
   (row loc) => 0
   (col loc) => 1
   (add-delta loc [1 1]) => [1 2]))