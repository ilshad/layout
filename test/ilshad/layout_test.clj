(ns ilshad.layout-test
  (:require [ilshad.layout :refer [layout wrap-layout prevent-layout]]
            [ring.util.response :refer [response]]
            [ring.mock.request :refer [request]]
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

(defn template-1
  [_ content]
  (str "foo " (str content)))

(deftest test-layout-response-string
  (is (= (layout (request :get "/") "bar" template-1)
         {:status 200
          :headers {"Content-Type" "text/html; charset=utf-8"}
          :body "foo bar"})))
