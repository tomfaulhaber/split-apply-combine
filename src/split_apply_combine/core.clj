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
  "Returns a function that applies f to each of the columns in cols and
   produces a new dataset with only the modified columns"
  [cols f]
  (fn [data]
    (let [cols (cond
                (= cols :all)      (col-names data)
                (= cols :num)      (numeric-cols data)
                (not (coll? cols)) [cols]
                :else              cols)
          columns (for [col cols]
                    (let [result (f ($ col data))]
                      (if (coll? result) result [result])))]
     (dataset-from-columns cols columns))))

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
  "If kw is the name of a column in data, return that column. Otherwise
   just return kw itself. If map is true, we return an seq of keywords 
   as long as their are rows in the data."
  [kw map? data]
  (if (some #{kw} (col-names data))
    ($ kw data)
    (if map? (repeat (nrow data) kw) kw)))

(defn build-keyword-map
  "Find all the unique keywords in expr and make a gensymed symbol for each. Return 
   a map of the keywords to their corresponding symbols. This is used as a support 
   function in the transform macro."
  [expr]
  (let [kws (set (filter keyword? (flatten expr)))]
    (into {} (for [kw kws] [kw (gensym (str "kw-" (name kw) "-"))]))))

(defn convert-keywords 
  "Walk expr and change all the keywords into the symbol specified for that keyword
   in the kw-map. This is a support function for the transform macro."
  [expr kw-map]
  (walk/postwalk 
   #(if (keyword? %) (kw-map %) %) 
   expr))

(defn build-transform-fn
  "Builds a function for expr depending on op. The function will take a dataset
   and return the value of expr. Keywords will be interpreted as shorthand for the
   corresponding column in the dataset when there is one. Evaluation of the columns
   is factored out, so that it only happpens once no matter how many times the 
   keyword is referenced.

   This is a support function for the transform macro. The the documentation there 
   for the information on the available values for op."
  [op expr]
  (let [data-param (gensym "data-")
        dummy-param (gensym "dummy-")
        kw-map (build-keyword-map expr)
        have-map? (pos? (count kw-map))] 
    `(fn [~data-param]
       (let [~@(apply concat (for [[kw kw-sym] kw-map] [kw-sym `(col-or-keyword ~kw ~(= op '=*) ~data-param)]))]
         ~(condp = op
            '= (convert-keywords expr kw-map)
            '=* `(map (fn [~@(if have-map? (vals kw-map) [dummy-param])] 
                        ~(convert-keywords expr kw-map)) 
                      ~@(if have-map?
                          (vals kw-map)
                          [`(repeat (nrow ~data-param) nil)]))
            (throw (Exception. "transform expressions must use one of = or =* for the operator")))))))


(defn transform*
  "Returns a function that transforms a dataset by changing or adding columns. 

   The transforms are in pairs with a column name and a transform function
   that will produce a the new value for that column. The transform functions will
   be passed a single argument, the dataset that transform* was called with.

   If the column name was part of the original dataset, it is replaced in the output. 
   Otherwise, it is added to the output.

   Example: ((transform* :Change #(sac/diff0 ($ :Close %)) 
                         :Relative #(let [close ($ :Close %)] 
                                      (map (fn [val] (/ val (first close))) close)))
              stock-data)

   For a version that allows for terser expression of the transforms, see the
   transform macro."
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
  "Returns a function that transforms a dataset by changing or adding columns.

   Each transform is expressed as a triple:
       column-name operator transform-expr

   There are two operators available:
       =    Evaluates the expression on the whole dataset and return a result 
            for the specified column.
       =*   Evaluates the expression for each row of the row of the dataset and 
            return a result for that row in the specified column (that is, 
            perform an implicit map).

   Within each transform-expr, keywords will be interpreted as referring to the
   named column in the input dataset. The implicit sel operation is performed
   exactly once for each function invocation regardless of how many times the
   keyword is used in the transform-expr.

   If the column name was part of the original dataset, it is replaced in the output. 
   Otherwise, it is added to the output.

   Example:
       ((transform
          :Average  =* (/ (+ :Open :Close) 2)
          :Change   = (diff0 :Close) 
          :Relative = (map #(/ % (first :Close)) :Close)
        stock-data)

   The transform macro is a wrapper around the transform* function. Sometimes, using
   transform* directly is a better choice."
  [& transforms]
  `(transform* ~@(apply concat 
                        (for [[col op xform] (partition 3 transforms)]
                          [col (build-transform-fn op xform)]))))
