(ns ilshad.layout-test
  (:require [ring.util.response :refer [response]]
            [ring.mock.request :refer [request]]
            [ring.adapter.jetty :refer [run-jetty]]
            [net.cgrand.enlive-html :as html]
            [clojure.test :refer [deftest is are]]
            [ilshad.layout :refer [layout wrap-layout]]
            [ilshad.layout-test.mock-app-1 :as mock-app-1]))

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

;; funcitonal tests
(declare test-port)

(defn run-test-app
  [app f]
  (let [server (run-jetty app {:port 0 :join? false})
        port (-> server .getConnectors first .getLocalPort)]
    (def test-port port)  ;; would use with-redefs, but can't test on 1.2
    (reset! missles-fired? false)
    (try
      (f)
      (finally
        (.stop server)))))
