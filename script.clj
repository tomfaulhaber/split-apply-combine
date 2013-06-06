;;; Block: Load the namespaces

(use 'incanter.core)
(require ['incanter.charts :as 'chart])
(require ['incanter.io :as 'iio])
(require ['incanter.stats :as 'stats])
(require ['clj-time.core :as 'time])
(require ['clj-time.coerce :as 'coerce])
(require ['split-apply-combine.core :as 'sac])
(require ['split-apply-combine.ply :as 'ply])
(require ['split-apply-combine.stock :as 'stock])
(require ['split-apply-combine.cpu :as 'cpu])

(defmethod print-method incanter.core.Dataset [o, ^java.io.Writer w]
   (binding [*out* w]
     (clojure.pprint/print-table (:column-names o) (:rows o))))

;;; Block: Look at the data

(def cpu-data (iio/read-dataset "data/scdb_agent_10.0.1.101.cpu" :delim \| :header true))

;;; Block: Load data

(def cpu-data 
  (reduce 
   conj-rows
   (for [[ip cpu-file] cpu/cpu-files] 
     (do
       (println cpu-file)
       (->> (iio/read-dataset cpu-file :delim \| :header true)
            ((sac/transform
              :core      =* (keyword :core)
              :timestamp =* (coerce/from-long (* :timestamp 1000))
              :ip        =* ip)))))))

;;; Block: Normalize data #1

(def cpu-data (ply/ddply [:ip :core] 
                         (sac/colwise :all #(if (number? (first %)) 
                                              (sac/diff %)
                                              (drop 1 %))) 
                         cpu-data))

;;; Block: Normalize data #2

(def cpu-data ((sac/transform :user   =* (/ :user   (+ :user :system :iowait :idle))
                              :system =* (/ :system (+ :user :system :iowait :idle))
                              :iowait =* (/ :iowait (+ :user :system :iowait :idle))
                              :idle   =* (/ :idle   (+ :user :system :iowait :idle))
                              :load   =* (/ (+ :user :system)   (+ :user :system :iowait :idle)))
               cpu-data))

;;; Block: Summarize all cores into a single number 
(def system-data ($order :timestamp :asc
                        (ply/ddply [:timestamp :ip] (sac/colwise :num sum) cpu-data)))

;;; Block: Graph the summary for each system
(ply/d_ply :ip 
           #(view 
             (chart/bar-chart :timestamp :load :data % 
                              :title (str "CPU Load for " 
                                          (first ($ :ip %))))) 
           system-data)

;;; Block: Get the mean for each system for minute
(def data-by-minute 
  ($order :timestamp :asc
          (ply/ddply [[:timestamp (time/minus 
                                   :timestamp 
                                   (time/secs (time/sec :timestamp)))] 
                      :ip] 
                     (sac/colwise :num stats/mean) 
                     system-data)))

;;; Block: Graph the summary for each system by minute
(ply/d_ply :ip 
           #(view 
             (chart/bar-chart :timestamp :load :data % 
                              :title (str "CPU Load for " 
                                          (first ($ :ip %)))))
           data-by-minute)

;;; Block: End
