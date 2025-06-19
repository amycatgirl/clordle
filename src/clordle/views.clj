(ns clordle.views
  (:require
   [hiccup.page :as page]))

(defn make-head [title & {:keys [with-game-logic] :or {with-game-logic false}}]
  [:head
   [:title title]
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width,initial-scale=1.0"}]
   (when with-game-logic
     [:script {:type "text/javascript"
               :src "js/game.js" ;; I specifically need defer here, which hiccup does not let me use when I do page/include-js
               :defer "true"}])
   (page/include-css "css/game.css")])

(def main-page
  (page/html5
   (make-head "Clordle"
              :with-game-logic true)
   [:header
    [:div.center-wordmark
     [:h1 "Clordle"]]
    [:div.actions
     [:span "gh"]]]
   [:div#game
    [:div.golumn ;; as in game column, not the lord of the rings annoying gremlin
     (for [rid (range 0 6)]
       [:div.row {:id rid}
        (for [_ (range 0 5)]
          [:div.cell.none])])]
    [:div.form
     [:input {:type "text" :maxlength 5 :autofocus ""}]
     [:div#used-chars]]]
   [:dialog#youwin
    [:h2 "Congratulations!"]
    [:p.correct-word "The word was "]
    [:button.close "Close"]]
   [:dialog#youlose
    [:h2 "Better luck next time"]
    [:p.correct-word "The word was "]
    [:button.close "Close"]]))
