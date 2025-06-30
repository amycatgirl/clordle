(ns clordle.api
  (:require [clojure.string :as str]
            [clordle.words :as words]
            [clordle.db :as db]
            [hiccup.core :refer [html]] ;; [TODO] Use hiccup2.core instead of hiccup.core
            ))

(def secret-word (words/rand-word))

(defn handshake []
  {:status 200
   :content-type "text/plain"
   :body (str "pad" secret-word "mat")})

(defn score-word-match
  [guess answer]
  (let [ans-length (count answer)
        ans-char-freq (frequencies answer)
        guess-char-freq (frequencies guess)]

    (map-indexed
     (fn [idx guess-char]
       (let [ans-char (if (< idx ans-length)
                        (nth answer idx)
                        nil)
             guess-char-total-count (get guess-char-freq guess-char 0)
             ans-char-overall-count (get ans-char-freq guess-char 0)]

         (cond
           (= guess-char ans-char) 2
           (> ans-char-overall-count 0) (if (> guess-char-total-count ans-char-overall-count)
                                          0
                                          1)
           :else 0)))
     guess)))

(defn respond-with-hint [id guess]
  (when (not (= 5 (count guess)))
    {:status 400
     :content-type "text/plain"
     :body "Invalid guess."})
  (let [answer-for-id (db/get-puzzle id)]
    {:status 200
     :content-type "text/plain"
     :body (str/join "" (score-word-match
                         (str/lower-case guess)
                         answer-for-id))}))

(defn respond-with-result [id key]
  (if (= key secret-word)
    (let [answer-for-id (db/get-puzzle id)]
      {:status 200
       :content-type "text/plain"
       :body answer-for-id})

    {:status 400
     :content-type "text/plain"
     :body "Bad key."}))

(defn respond-with-all-puzzles []
  (html
   [:div.puzzle-list
    [:ul
     (for [puzzle (db/get-all-puzzles)]
       [:li [:p (str "Puzzle No." (:PUZZLES/ID puzzle)) [:a.btn.attempt {:href (str "puzzle/" (:PUZZLES/ID puzzle))} "Attempt"]]])]]))
