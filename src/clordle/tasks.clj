(ns clordle.tasks
  (:require
   [clordle.words :as words]
   [clordle.db :as db]
   [chime.core :as chime])
  (:import [java.time Instant Duration]))

(defn schedule-new-puzzle
  []
  (-> (chime/periodic-seq (Instant/now) (Duration/ofDays 1))
      (chime/chime-at
       (fn [time]
         (db/add-puzzle time (words/rand-word))))))
