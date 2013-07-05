(ns ilshad.layout-test
  (:require [ilshad.layout :refer [layout wrap-layout prevent-layout]]
            [ring.util.response :refer [response]]
            [ring.mock.request :refer [request]]
            [net.cgrand.enlive-html :as html]
            [clojure.test :refer [deftest is are]]))

(defn handler-1
  [req]
  (response "foo"))

(deftest test-prevent-layout
  (let [h (prevent-layout handler-1)]
    (is (= (h (request :get "/"))
           {:status 200
            :headers {}
            :body "foo"
            :layout nil}))))

(html/deftemplate template-1
  "template.html"
  [req content]
  [:article] (html/content content))

(deftest test-layout-deftemplate-string
  (let [resp (layout (request :get "/") "bar" template-1)]
    (is (= (:status resp) 200))
    (is (= (:headers resp) {"Content-Type" "text/html; charset=utf-8"}))
    (is (= (:body resp) '("<html>" "\n  " "<body>" "\n    "
                          "<header>Title</header>" "\n    "
                          "<article>" "bar" "</article>" "\n  "
                          "</body>" "\n\n" "</html>")))))
