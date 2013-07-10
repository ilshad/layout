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
  [req content & {:keys [mode]}]
  [:#main] (case mode
             :enlive-snippet (html/content content)
             :html-string (html/html-content content)
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

(deftest test-layout-default-deftemplate-response-snippet
  (let [resp (layout (request :get "/")
                     (response (snippet-1))
                     template-1)]
    (is (= (:status resp) 200))
    (is (= (:headers resp) {}))
    (is (= (:body resp) '("<html>" "\n  " "<body>" "\n    "
                          "<h1>Title\n    </h1>"
                          "<div id=\"main\">"
                          "<div id=\"snippet-1\">foo</div>"
                          "</div>" "\n  "
                          "</body>" "\n\n" "</html>")))))

(deftest test-layout-default-deftemplate-response-html
  (let [resp (layout (request :get "/")
                     (-> (response "<em>foo</em>")
                         (assoc :layout {:mode :html}))
                     template-1)]
    (is (= (:status resp) 200))
    (is (= (:headers resp) {}))
    (is (= (:body resp) '("<html>" "\n  " "<body>" "\n    "
                          "<h1>Title\n    </h1>"
                          "<div id=\"main\">" "<em>foo</em>" "</div>" "\n  "
                          "</body>" "\n\n" "</html>")))))
