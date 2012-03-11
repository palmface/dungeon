(ns dungeon.core
  (:use [dungeon.game-state :only [read-game-state]]
        [dungeon.gui :only [run-game]]))

(def dungeon
  ["#####@####"
   "#M#......#"
   "#.#......#"
   "#.#......#"
   "#.#......#"
   "#.#......#"
   "#.####...#"
   "#...#.m..#"
   "#.m.#.M.##"
   "#........#"
   "##########"])

(defn -main []
  (run-game (read-game-state dungeon)))