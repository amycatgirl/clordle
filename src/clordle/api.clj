(ns clordle.api
  (:require [clojure.string :as str]
            [clordle.words :as words]))

(def debug-guess-word (str/split (words/rand-word) #""))
(def secret-word (words/rand-word))

(defn handshake []
  {:status 200
   :content-type "text/plain"
   :body (str "pad" secret-word "mat")})

(defn make-hint-seq [guess]
  (let [target-chars (set debug-guess-word)]
    (reduce
      (fn [acc [idx char]]
        (let [state (cond
                      (= (get debug-guess-word idx) char) 2
                      (contains? target-chars char) 1
                      :else 0)]
          (print state)
          (conj acc state)))
      []
      (map-indexed (fn [idx char] [idx char]) guess))))

(defn respond-with-hint [guess]
  (when (not (= 5 (count guess)))
    {:status 400
     :content-type "text/plain"
     :body "Too large."})
  {:status 200
   :content-type "text/plain"
   :body (str/join "" (make-hint-seq (str/split (str/lower-case guess) #"")))}
  )

(defn respond-with-result [key]
  (if (= key secret-word)
    {:status 200
     :content-type "text/plain"
     :body (str/join debug-guess-word)}
    {:status 400
     :content-type "text/plain"
     :body "Bad key."}))
