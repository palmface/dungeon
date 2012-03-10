(ns dungeon.gui
  (:use dungeon.game-state
        dungeon.dungeon)
  (:import [javax.swing JFrame JPanel SwingUtilities]
           [java.awt Graphics Color Dimension]
           [java.awt.event KeyEvent KeyListener]))

(def cell-width 15)
(def cell-height 15)

(def color-for {:player Color/red
                :floor  Color/gray
                :wall Color/black})

(defn- draw-tile [[row col :as location] dungeon-state graphics]
  (doto graphics
    (.setColor (color-for (tile-at dungeon-state location)))
    (.fillRect (* col cell-width) (* row cell-height)
               cell-width         cell-height)))

(defn- draw-state [dungeon-state graphics]
  (doseq [row (range (height dungeon-state))
          col (range (width dungeon-state))]
    (draw-tile [row col] dungeon-state graphics)))

(defn move [direction]
  (fn [dungeon-state]
    (move-player dungeon-state direction)))

(def action-for
  {KeyEvent/VK_W (move :north)
   KeyEvent/VK_A (move :west)
   KeyEvent/VK_S (move :south)
   KeyEvent/VK_D (move :east)})

(defn- make-canvas-proxy [dungeon-state]
  (proxy [JPanel KeyListener] []
    (paintComponent [graphics]
      (do (proxy-super paintComponent graphics)
        (draw-state @dungeon-state graphics)))
    (getPreferredSize [] 
      (Dimension. (* cell-width (width @dungeon-state))
                  (* cell-height (height @dungeon-state))))
    (keyPressed [event]
      (let [key (.getKeyCode event)
            action (action-for key)]
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
      (.show))
    (.requestFocusInWindow canvas)
    frame))

(defn- make-game [state]
  (make-window (atom state)))

(defn run-game [state]
  (SwingUtilities/invokeLater (fn [] (make-game state))))
