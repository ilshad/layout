(ns ilshad.layout-test-mock-app
  (:require [ilshad.layout :refer [wrap-layout]]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found]]
            [net.cgrand.enlive-html :as html]
            [ring.util.response :refer [response]]))

(html/deftemplate template-1
  "template.html"
  [req content params]
  [:#main] (if (= (:mode params) :string)
             (html/html-content content)
             (html/content content)))

(html/defsnippet snippet-1
  "template.html"
  [:#snippet-1]
  [])

(defroutes app*
  (GET "/" _ (snippet-1))
  (not-found "<h1>Not found</h1>"))

(def missles-fired? (atom false))

(def app
  (-> app*
      (wrap-layout template-1)
      site))
