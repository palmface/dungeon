(ns dungeon.gui
  (:require [dungeon.game-state :as gs]
            [dungeon.tile-content :as tc]
            [dungeon.monster :as m])
  (:import [javax.swing JFrame JPanel SwingUtilities]
           [java.awt Graphics Color Dimension]
           [java.awt.event KeyEvent KeyListener]))

(def cell-width 10)
(def cell-height 10)

(def color-for {:player Color/red
                :floor  Color/gray
                :item Color/blue
                :wall Color/black
                :monster Color/green
                :Monster Color/yellow})

(defn- set-color [color graphics]
  (doto graphics
    (.setColor color)))

(defn- fill-rect [[x y] with height graphics]
  (doto graphics
    (.fillRect x y with height)))

(defn- draw-base [[x y] tile-content graphics]
  ((comp (partial fill-rect
                  [(* x cell-width)
                   (* y cell-height)]
                  cell-width cell-height)
         (partial set-color
                  (color-for (tc/base tile-content))))
   graphics))

(defn- draw-items [[x y] tile-content graphics]
  (if (tc/has-items? tile-content)
    ((comp (partial fill-rect
                    [(+ (* x cell-width)
                        (int (/ cell-width 4)))
                     (+ (* y cell-height)
                        (int (/ cell-height 4)))]
                    (int (/ cell-width 2))
                    (int (/ cell-height 2)))
           (partial set-color
                    (color-for :item)))
     graphics)
    graphics))

(defn- draw-monster [[x y] tile-content graphics]
  (if (tc/has-monster? tile-content)
    ((comp (partial fill-rect
                    [(* x cell-width)
                     (* y cell-height)]
                    cell-width cell-height)
           (partial set-color
                    (color-for (m/kind (tc/monster tile-content)))))
     graphics)
    graphics))

(defn- draw-tile [graphics [x y :as location] tile-content]
  ((comp (partial draw-monster location tile-content)
         (partial draw-items location tile-content)
         (partial draw-base location tile-content))
   graphics))

(defn- draw-player [dungeon-state graphics]
  (let [[y x] (gs/player-location dungeon-state)]
    ((comp (partial fill-rect
                    [(* x cell-width)
                     (* y cell-height)]
                    cell-width cell-height)
           (partial set-color
                    (color-for :player)))
     graphics)))

(defn- draw-state [dungeon-state graphics]
  (doseq [row (range (gs/height dungeon-state))
          col (range (gs/width dungeon-state))]
    (draw-tile graphics [col row] (gs/tile-at dungeon-state [row col])))
  (draw-player dungeon-state graphics))

(defn move [direction]
  (fn [dungeon-state]
    (gs/move-player dungeon-state direction)))

(def action-for
  {KeyEvent/VK_W (move :north)
   KeyEvent/VK_A (move :west)
   KeyEvent/VK_S (move :south)
   KeyEvent/VK_D (move :east)
   KeyEvent/VK_E gs/pick-item})

(defn- make-canvas-proxy [dungeon-state]
  (proxy [JPanel KeyListener] []
    (paintComponent [graphics]
      (do (proxy-super paintComponent graphics)
          (draw-state @dungeon-state graphics)))
    (getPreferredSize [] 
      (Dimension. (* cell-width (gs/width @dungeon-state))
                  (* cell-height (gs/height @dungeon-state))))
    (keyPressed [event]
      (let [key (.getKeyCode event)
            action (action-for key identity)]
        (swap! dungeon-state action)
        (.repaint this)))
    (keyReleased [event])
    (keyTyped [event])))

(defn- make-canvas [state]
  (let [canvas (make-canvas-proxy state)]
    (.addKeyListener canvas canvas)
    canvas))

(defn- make-frame []
  (JFrame. "EPIC DUNGEON"))

(defn- make-window [state]
  (let [frame (make-frame)
        canvas (make-canvas state)]
    (doto frame
      (.setContentPane canvas)
      (.pack)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.show))
    (.requestFocusInWindow canvas)
    frame))

(defn- make-game [state]
  (make-window (atom state)))

(defn run-game [state]
  (SwingUtilities/invokeLater (fn [] (make-game state))))
