(ns clordle.core
  (:gen-class)
  (:require
   [compojure.core :refer [defroutes context GET POST]]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [clordle.views :as views]
   [clordle.api :as api]
   [clordle.tasks :as tasks]
   [ring.util.request :refer [body-string]]
   [clordle.db :refer [setup-database-for-first-use]]))

(defroutes app-routes
  (GET "/" [] views/main-page)
  (GET "/puzzle/:id" [] views/main-page)
  (context "/api" []
           (POST "/guess/:id" [id :as request]
                 (api/respond-with-hint id (body-string request)))
           (POST "/giveup/:id" [id :as request]
                 (api/respond-with-result id (body-string request)))
           (GET "/handshake" []
                (api/handshake))
           (GET "/puzzles" []
                (api/respond-with-all-puzzles)))
  (route/resources "/")
  (route/not-found "<h1>Sorry, we couldn't find the page you were looking for.</h1>"))

(def app
  (-> app-routes
      (wrap-defaults (-> site-defaults
                         (assoc-in [:security :anti-forgery] false)))))

(defn -main
  []
  (setup-database-for-first-use)
  (tasks/schedule-new-puzzle)
  (jetty/run-jetty #'app {:port 3000}))
