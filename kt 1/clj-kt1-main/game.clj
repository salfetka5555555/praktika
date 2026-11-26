
(def low (atom 0))
(def high (atom 0))
(def guess (atom 0))

(defn start [n m]
  (reset! low n)
  (reset! high m)
  (reset! guess (quot (+ n m) 2))
  "Я готов...")

(defn guess-my-number []
  @guess)

(defn smaller []
  (reset! high (dec @guess))
  (reset! guess (quot (+ @low @high) 2))
  @guess)

(defn bigger []
  (reset! low (inc @guess))
  (reset! guess (quot (+ @low @high) 2))
  @guess)
