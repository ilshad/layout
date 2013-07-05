(ns ilshad.layout-test
  (:require [ilshad.layout :refer [layout wrap-layout prevent-layout]]
            [ring.util.response :refer [response]]
            [ring.mock.request :refer [request]]
            [net.cgrand.enlive-html :as html]
            [clojure.test :refer [deftest is]]))

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
  ""
  [req content]
  )

(defn template-1
  [_ content]
  (html/template )
  (str "foo " content))

(deftest test-layout-response-string
  (is (= (layout (request :get "/")
                 "bar"
                 template-1)
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body "foo bar"})))
