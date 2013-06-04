(defproject split-apply-combine "0.1.0-SNAPSHOT"
  :description "Sample Code for Tom Faulhaber's presentation on split-apply-combine at the June 2013 San Francisco Clojure meetup"
  :url "https://github.com/tomfaulhaber/split-apply-combine"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/data.csv "0.1.2"]
                 [org.clojure/clojure "1.5.1"]
                 [incanter "1.5.0-SNAPSHOT"]
                 [clj-http "0.7.2"]     ;not sure if I still need this
                 [clj-time "0.5.1"]]
  :jvm-opts ["-Xmx4g"])
