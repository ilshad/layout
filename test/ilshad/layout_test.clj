(ns ilshad.layout-test
  (:require [ilshad.layout :refer [layout wrap-layout]]
            [ring.util.response :refer [response]]
            [ring.mock.request :refer [request]]
            [net.cgrand.enlive-html :as html]
            [clojure.test :refer [deftest is are]]))

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

(deftest test-layout-default-deftemplate-body-snippet
  (let [resp (layout (request :get "/")
                     (snippet-1)
                     template-1)]
    (is (= (:status resp) 200))
    (is (= (:headers resp) {"Content-Type" "text/html; charset=utf-8"}))
    (is (= (:body resp) '("<html>" "\n  " "<body>" "\n    "
                          "<h1>Title\n    </h1>"
                          "<div id=\"main\">"
                          "<div id=\"snippet-1\">foo</div>"
                          "</div>" "\n  "
                          "</body>" "\n\n" "</html>")))))

(deftest test-layout-default-deftemplate-body-string-params
  (let [resp (layout (request :get "/")
                     "<h1>Foo</h1>"
                     template-1
                     {:mode :string})]
    (is (= (:status resp) 200))
    (is (= (:headers resp) {"Content-Type" "text/html; charset=utf-8"}))
    (is (= (:body resp) '("<html>" "\n  " "<body>" "\n    "
                          "<h1>Title\n    </h1>"
                          "<div id=\"main\">"
                          "<" "h1" ">" "Foo" "</" "h1" ">"
                          "</div>" "\n  "
                          "</body>" "\n\n" "</html>")))))
