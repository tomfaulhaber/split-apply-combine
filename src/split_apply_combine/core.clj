(ns split-apply-combine.core
  "The general functions for working with data using the split apply combine model."
  (:require 
   [clojure.data.csv :as csv]
   [clojure.java.io :as jio]
   [clojure.pprint :as pp]
   [clojure.walk :as walk])
  (:use 
   [incanter.core :only [$ $data col-names dataset conj-cols with-data]]))

(defn diff 
  "Takes a seqable (coll) and returns a seq of the differences between each element.
The resulting seq is one shorter than the input"
  [coll]
  (when-let [nxt (next coll)]
    (lazy-seq 
     (cons 
      (- (first nxt) (first coll))
      (diff nxt)))))

(defn diff0 
  "Like diff, but adds a zero as the first element in order to 
have the result be the same length as coll"
  [coll]
  (cons 0 (diff coll)))

(defn unique 
  "Returns unique values of a coll, sorted by default"
  ([coll] (unique coll true))
  ([coll sorted?] 
     (let [s (set coll)]
       (if sorted? (sort s) s))))

(defn nrow 
  "Return the number of rows in a dataset" 
  ([] (nrow $data))
  ([data] (count (:rows data))))

(defn numeric-cols 
  "Return the names of the numeric columns from a dataset. A column is considered
numeric if its first row is numeric."
  [data]
  (when-let [row (first (:rows data))]
    (filter #(number? (get row %)) (col-names data))))

(defn dataset-from-columns [column-names columns]
  (dataset column-names (apply (partial map vector) columns)))

(defn colwise 
  "Apply f to each of the columns in cols and produce a new dataset with the original columns 
and the modified columns"
  [f cols data]
  (let [all-cols (col-names data) 
        cols (cond 
              (= cols :all)  all-cols
              (= cols :num) (numeric-cols data)
              (not (coll? cols)) [cols]
              :else cols)
        col-set (set cols)
        columns (for [col all-cols :let [base ($ col data)]] 
                  (if (col-set col)
                    (f base)
                    base))]
    (dataset-from-columns all-cols columns)))

(defn add-identifier 
  "Add a constant identifier column to the dataset"
  [col-name val data]
  (let [nrow (count (:rows data))
        val-column (dataset [col-name] (repeat nrow [val]))]
    (conj-cols val-column data)))

(defn pr-data 
  "Use print-table to print a dataset in a nice tabular format"
  [data]
  (pp/print-table (col-names data) (:rows data)))


;;; TODO: this should be generalized to have all the options of read-dataset
(defn write-dataset-csv 
  "Writes a data set as a CSV file, with headers"
  [file-name data]
  (let [cols (col-names data)] 
    (with-open [out-file (jio/writer file-name)]
      (pp/cl-format out-file "~{~a~^,~}~%" (map name cols))
      (csv/write-csv out-file (map (fn [row] (map #(get row %) cols)) (:rows data))))))

(defn col-or-keyword 
  [kw data]
  (if (some #{kw} (col-names data))
    ($ kw data)
    kw))

(defn convert-keywords 
  [expr]
  (let [data-param (gensym "data")] 
    `(fn [~data-param] ~(walk/postwalk #(if (keyword? %) 
                                    `(col-or-keyword ~% ~data-param) 
                                    %) 
                                 expr))))

(defn transform*
  [& transforms]
  (let [pairs (partition 2 transforms)
        xform-cols (map first pairs)
        xform-func (apply juxt (map second pairs))]
    (fn [data] 
      (let [xform-data (dataset-from-columns xform-cols (xform-func data))
            base-cols (col-names data)
            union-cols (concat base-cols (filter (complement (set base-cols)) xform-cols))]
        (dataset union-cols (map #(merge %1 %2) (:rows data) (:rows xform-data)))))))

(defmacro transform
  [& transforms]
  `(transform* ~@(apply concat 
                        (for [[col xform] (partition 2 transforms)]
                          [col (convert-keywords xform)]))))
