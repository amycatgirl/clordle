(ns clordle.db
  (:require
   [clordle.words :as words]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

(def db {:dbtype "h2" :dbname "data"})
(def ds (jdbc/get-datasource db))

(defn setup-database-for-first-use []
  (jdbc/execute! ds ["
    CREATE TABLE IF NOT EXISTS PUZZLES (
        id int auto_increment primary key,
        time_created varchar(50),
        word varchar(5)
    )"])
  (when (seq? (sql/query ds ["SELECT * FROM PUZZLES"]))
    ;; Populate the database if it is empty!
    (sql/insert! ds :PUZZLES {:WORD (words/rand-word)})))

(defn add-puzzle [time-created word]
  (when (= (count word) 5)
    (sql/insert! ds :PUZZLES {:WORD word
                              :TIME_CREATED time-created})))

(defn get-all-puzzles []
  (sql/query ds ["SELECT * FROM PUZZLES ORDER BY ID DESC"]))

(defn get-puzzle
  [id] (if (number? id)
         (:PUZZLES/WORD (sql/get-by-id ds :PUZZLES id))
         (:PUZZLES/WORD (first (sql/query ds ["SELECT WORD FROM PUZZLES ORDER BY ID DESC LIMIT 1"])))))
