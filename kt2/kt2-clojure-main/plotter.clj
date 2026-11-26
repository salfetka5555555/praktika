(ns plotter.core)

;; --- Типы (через map) ---

(defn make-state [position angle color carriage]
  {:position position
   :angle angle
   :color color
   :carriage carriage})

;; --- Вспомогательные функции ---

(defn deg->rad [angle]
  (* angle (/ Math/PI 180.0)))

(defn calc-new-position [distance angle {:keys [x y]}]
  (let [rad (deg->rad angle)
        new-x (+ x (* distance (Math/cos rad)))
        new-y (+ y (* distance (Math/sin rad)))]
    {:x (Math/round new-x)
     :y (Math/round new-y)}))

(defn draw-line [printer from to color]
  (printer (str "...Чертим линию из (" (:x from) ", " (:y from)
                ") в (" (:x to) ", " (:y to)
                ") используя " color " цвет.")))

;; --- Команды плоттера (ЧИСТЫЕ функции) ---

(defn move [printer distance state]
  (let [new-pos (calc-new-position distance (:angle state) (:position state))]
    (when (= (:carriage state) :down)
      (draw-line printer (:position state) new-pos (:color state)))
    (when (= (:carriage state) :up)
      (printer (str "Передвигаем на " distance
                    " от точки (" (get-in state [:position :x])
                    ", " (get-in state [:position :y]) ")")))
    (assoc state :position new-pos)))

(defn turn [printer angle state]
  (printer (str "Поворачиваем на " angle " градусов"))
  (update state :angle #(mod (+ % angle) 360.0)))

(defn carriage-up [printer state]
  (printer "Поднимаем каретку")
  (assoc state :carriage :up))

(defn carriage-down [printer state]
  (printer "Опускаем каретку")
  (assoc state :carriage :down))

(defn set-color [printer color state]
  (printer (str "Устанавливаем " color " цвет линии"))
  (assoc state :color color))

(defn set-position [printer position state]
  (printer (str "Устанавливаем позицию каретки в ("
                (:x position) ", " (:y position) ")"))
  (assoc state :position position))

;; --- Высокоуровневые операции (без циклов) ---

(defn repeat-command [n f state]
  (nth (iterate f state) n))

(defn draw-triangle [printer size state]
  (-> state
      (carriage-down printer)
      (repeat-command
       3
       (fn [s]
         (-> s
             (move printer size)
             (turn printer 120.0))))
      (carriage-up printer)))

(defn draw-square [printer size state]
  (-> state
      (carriage-down printer)
      (repeat-command
       4
       (fn [s]
         (-> s
             (move printer size)
             (turn printer 90.0))))
      (carriage-up printer)))

;; --- Запуск ---

(def printer println)

(def initial-state
  (make-state {:x 0 :y 0} 0.0 "черный" :up))

(def final-state
  (-> initial-state
      (draw-triangle printer 100.0)
      (set-position printer {:x 10 :y 10})
      (set-color printer "красный")
      (draw-square printer 80.0)))
