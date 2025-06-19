(ns clordle.core
  (:require
   [compojure.core :refer [defroutes context GET POST]]
   [compojure.route :as route]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [clordle.views :as views]
   [clordle.api :as api]
   [ring.util.request :refer [body-string]]))

(defroutes app-routes
  (GET "/" [] views/main-page)
  (context "/api" []
           (POST "/guess/:id" [id :as request]
                 (api/respond-with-hint (body-string request)))
           (POST "/giveup/:id" [id :as request]
                 (api/respond-with-result (body-string request)))
           (GET "/handshake" []
                (api/handshake)))
  (route/resources "/")
  (route/not-found "<h1>Sorry, we couldn't find the page you were looking for.</h1>"))

(def app
  (-> app-routes
      (wrap-defaults (-> site-defaults
                         (assoc-in [:security :anti-forgery] false)))))

(defn -main
  []
  (jetty/run-jetty #'app {:port 3000}))
