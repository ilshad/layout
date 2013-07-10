(ns ilshad.layout
  (:require [ring.util.response :refer [response?]]
            [compojure.response :refer [render]]))

(defn- update-response-after-flash
  "Force setting session to clean up flash."
  [resp req]
  (if (:session resp)
    resp
    (if (:flash req)
      (assoc resp :session (:session req))
      resp)))

(defn- layout-include
  [resp req template params]
  (if (response? resp)
    (assoc resp :body (template req (:body resp) params))
    (template req resp params)))

(defn layout
  [req resp template & params]
  (-> resp
      (layout-include req template params)
      (render req)
      (update-response-after-flash req)))

(defn wrap-layout
  [handler spec]
  (let [spec (if (map? spec)
               spec
               {:default spec})]
    (fn [req]
      (let [resp (handler req)
            params (:layout resp)]
        (if (true? (:prevent params))
          resp
          (layout req
                  resp
                  (spec (:name params :default))
                  params))))))

(defn prevent-layout
  [handler]
  (fn [req]
    (assoc (handler req) :layout {:prevent true})))
