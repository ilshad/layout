(ns ilshad.layout
  (:require [ring.util.response :refer [response?]]
            [compojure.response :refer [render]]))

(defn- create-predicate--prevent?
  [patterns]
  (if (empty? patterns)
    (fn [_] false)
    (let [match (apply some-fn (map #(partial re-find %) patterns))]
      #((complement nil?) (match %)))))

(defn- update-response-after-flash
  [resp req]
  (if (:session resp)
    resp
    (if (:flash req)
      (assoc resp :session (:session req))
      resp)))

(defn layout
  [req resp template & [params]]
  (-> (if (response? resp)
        (assoc resp :body (template req (:body resp) params))
        (template req resp params))
      (render req)
      (update-response-after-flash req)))

(defn wrap-layout
  [handler spec]
  (let [templates (:templates spec {:default spec})
        prevent? (create-predicate--prevent? (:prevent spec))]
    (fn [req]
      (let [resp (handler req)
            params (:layout resp)]
        (if (or (true? (:prevent params))
                (prevent? (:uri req)))
          resp
          (layout req
                  resp
                  (templates (:template params :default))
                  params))))))
