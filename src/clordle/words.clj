(ns clordle.words
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn read-lines-from-resource [path]
  (line-seq (io/reader (io/resource path))))

(defn rand-word
  []
  (let [wordlist (read-lines-from-resource "en_US.txt")]
    (->> wordlist
         (map str/trim)
         (filter (complement str/blank?))
         (rand-nth)
         (doall))))
