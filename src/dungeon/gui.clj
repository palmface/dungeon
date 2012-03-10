(ns dungeon.gui
  (:import [javax.swing JFrame JPanel SwingUtilities]
           [java.awt Graphics Color Dimension]
           [java.awt.event KeyListener]))

(def cell-width 15)
(def cell-height 15)

(def- color-for {:player Color/red
                 :floor  Color/grey})

(defn- draw-tile [[row col :as location] dungeon-state graphics]
  (doto graphics
    (.setColor (color-for (tile-at dungeon-state location)))
    (fill-rect (* col cell-width) (* row cell-height)
               cell-width         cell-height)))

(defn- draw-state [dungeon-state graphics]
  (doseq [row (range (height dungeon-state))
          col (range (width dungeon-state))]
    (draw-tile [row col] dungeon-state graphics)))

(defn move [direction]
  (fn [dungeon-state]
    (move-player dungeon-state direction)))

(def- action-for
  {\w (move :north)
   \a (move :west)
   \s (move :north)
   \d (move :east)})

(defn- make-canvas-proxy [dungeon-state]
  (proxy [JPanel KeyListener] []
    (paintComponent [graphics]
      (do (proxy-super paintComponent graphics)
        (draw-state @dungeon-state graphics)))
    (getPreferredSize [] 
      (Dimension. (* cell-width (width @dungeon-state))
                  (* cell-height (height @dungeon-state))))
    (keyPressed [event]
      (let [key (.getKeyChar event)
            action (action-for key)]
        (swap! dungeon-state action)
        (.repaint this)))))

(defn- make-frame []
  (JFrame. "EPIC DUNGEON"))

(defn- make-window [state]
  (let [frame (make-frame)
        canvas (make-canvas-proxy state)]
    (doto frame
      (.setContentPane canvas)
      (.pack)
      (.show))))

(defn- make-game [state]
  (make-window (atom state)))

(defn run-game [state]
  (SwingUtilities/invokeLater (fn [] make-game state)))
