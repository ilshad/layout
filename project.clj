(defproject com.ilshad/layout "0.1.0-SNAPSHOT"
  :description "Web Layout library"
  :url "https://github.com/ilshad/layout"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.0-beta2"]
                 [compojure "1.1.5"]
                 [enlive "1.0.1"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.5"]]
                   :resource-paths ["test/resources"]}})
