(ns split-apply-combine.cpu
  "Routines to manipulate CPU load data from a 38 minute test in a small cluster.
   Thanks to SpaceCurve, Inc. (http://www.spacecurve.com) for permission to use 
   this data."
  (:use [incanter.core :only [$ $order $where conj-rows sum view with-data]])
  (:require [incanter.charts :as chart]
            [incanter.io :as io]
            [incanter.stats :as stats]
            [clj-time.core :as time]
            [clj-time.coerce :as coerce]
            [split-apply-combine.core :as sac]
            [split-apply-combine.ply :as ply]))

(def cpu-files 
  "The list of IP address, file name pairs that we used for our CPU load demos."
  [["10.0.1.101" "data/scdb_agent_10.0.1.101.cpu"]
   ["10.0.1.102" "data/scdb_agent_10.0.1.102.cpu"]
   ["10.0.1.107" "data/scdb_agent_10.0.1.107.cpu"]
   ["10.0.1.109" "data/scdb_agent_10.0.1.109.cpu"]
   ["10.0.1.111" "data/scdb_agent_10.0.1.111.cpu"]
   ["10.0.1.112" "data/scdb_agent_10.0.1.112.cpu"]])

(defn load-cpu-data 
  "Load the CPU data from each of the files specified in cpu-files. 
   We normalize it slightly by adding a column with the IP address,
   converting the times to JODA DateTimes and turning the core ids
   into keywords."
  [] 
  (doall
   (reduce conj-rows
           (for [[ip cpu-file] cpu-files] 
             (do
               (println cpu-file)
               (->> (io/read-dataset cpu-file :delim \| :header true)
                    ((sac/transform
                      :core      =* (keyword :core)
                      :timestamp =* (coerce/from-long (* :timestamp 1000))
                      :ip        =* ip))))))))

(defn normalize-cpu-data 
  "Normalize CPU results by % within the interval and add a load factor"
  [cpu-data]
  (->> cpu-data
       (ply/ddply [:ip :core] 
              (sac/colwise :all #(if (number? (first %)) 
                                   (sac/diff %)
                                   (drop 1 %))))
       ((sac/transform :user   =* (/ :user   (+ :user :system :iowait :idle))
                       :system =* (/ :system (+ :user :system :iowait :idle))
                       :iowait =* (/ :iowait (+ :user :system :iowait :idle))
                       :idle   =* (/ :idle   (+ :user :system :iowait :idle))
                       :load   =* (/ (+ :user :system)   (+ :user :system :iowait :idle))))))

(defn load-average 
  "Reduce each normalized CPU measurement across cores to compute a real load average for each machine."
  [cpu-data]
  ($order :timestamp :asc
          (ply/ddply [:timestamp :ip] (sac/colwise :num sum) cpu-data)))

(defn rollup-to-minutes
  "Divide the data by IP and take the mean load for each minute's data"
  [data]
  ($order :timestamp :asc
          (ply/ddply [[:timestamp (time/minus 
                                   :timestamp 
                                   (time/secs (time/sec :timestamp)))] 
                      :ip] 
                     (sac/colwise :num stats/mean) 
                     data)))

(defn render-graphs
  "Draw a load graph for each node represented in data"
  [data]
  (ply/d_ply :ip 
           #(view 
             (chart/bar-chart :timestamp :load :data % 
                              :title (str "CPU Load for " 
                                          (first ($ :ip %))))) 
           data))

(defn read-to-render
  "Read the data from the files listed in cpu-data and render mean load by minute."
  []
  (->> (load-cpu-data)
       (normalize-cpu-data)
       (load-average)
       (rollup-to-minutes)
       (render-graphs)))

(defn data-by-cpu 
  "Take raw data read from a single file and get the data for a single core, as 
   differences between samples."
  [cpu data]
  (sac/colwise sac/diff [:user :nice :system :idle :iowait]
               ($where {:core cpu} data)))

(defn load-chart 
  "Do a simple load chart for a single core."
  [cpu data]
  (with-data (data-by-cpu cpu data)
    (chart/bar-chart :timestamp :user)))
