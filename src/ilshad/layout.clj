(ns ilshad.layout
  (:require [ring.util.response :refer [response?]]
            [compojure.response :refer [render]]
            [net.cgrand.enlive-html :refer [html-snippet]]))

(defn- update-response-after-flash
  "Force setting session to clean up flash."
  [resp req]
  (if (:session resp)
    resp
    (if (:flash req)
      (assoc resp :session (:session req))
      resp)))

(defn- cont
  [c]
  (if (string? c)
    (html-snippet c)
    c))

(defn- layout-include
  [resp req template params]
  (if (response? resp)
    (assoc resp :body (template req (cont (:body resp)) params))
    (template req (cont resp) params)))

(defn- prevent-layout?
  [resp]
  ; complex detect nil value because TODO multilple named layouts
  (and (map? resp)
       (contains? resp :layout)
       (nil? (:layout resp))))

(defn layout
  [req resp template params]
  (if (prevent-layout? resp)
    resp
    (-> resp
        (layout-include req template params)
        (render req)
        (update-response-after-flash req))))

(defn wrap-layout
  [handler spec]
  (let [spec (if (map? spec) spec {:default spec})]
    (fn [req]
      (let [resp (handler req)
            params (:layout resp {})]
        (layout req
                resp
                (spec (:name params) :default)
                (dissoc params :name))))))

(defn prevent-layout
  [handler]
  (fn [req]
    (assoc (handler req) :layout nil)))
