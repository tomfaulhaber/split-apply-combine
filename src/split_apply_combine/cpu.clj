(ns split-apply-combine.cpu
  (:use [incanter.core :only [$where conj-rows with-data]])
  (:require [incanter.charts :as chart]
            [incanter.io :as io]
            [clj-time.coerce :as coerce]
            [split-apply-combine.core :as sac]
            [split-apply-combine.ply :as ply]))

(def cpu-files [["10.0.1.101" "data/scdb_agent_10.0.1.101.cpu"]
                ["10.0.1.102" "data/scdb_agent_10.0.1.102.cpu"]
                ["10.0.1.107" "data/scdb_agent_10.0.1.107.cpu"]
                ["10.0.1.109" "data/scdb_agent_10.0.1.109.cpu"]
                ["10.0.1.111" "data/scdb_agent_10.0.1.111.cpu"]
                ["10.0.1.112" "data/scdb_agent_10.0.1.112.cpu"]])

(defn load-cpu-data [] 
  (doall
   (reduce conj-rows
           (for [[ip cpu-file] cpu-files] 
             (do
               (println cpu-file)
               (->> (io/read-dataset cpu-file :delim \| :header true)
                    ((sac/transform
                      :core (map keyword :core)
                      :timestamp (map #(coerce/from-long (* % 1000)) :timestamp)
                      :ip (map (constantly ip) :timestamp)))))))))

(defn normalize-cpu-data 
  "Normalize CPU results by % within the interval and add a load factor"
  [data] 
  (ply/ddply [:ip :core] #(sac/colwise :num sac/diff %) data))

(defn load-average 
  "Reduce each normalized CPU measurement across cores to compute a real load average for each machine."
  [data])

(defn data-by-cpu [cpu data]
  (sac/colwise sac/diff [:user :nice :system :idle :iowait]
               ($where {:core cpu} data)))

(defn load-chart [cpu data]
  (with-data (data-by-cpu cpu data)
    (chart/bar-chart :timestamp :user)))
