(ns split-apply-combine.ply
  "Implementation of the split-apply-combine functions, similar to R's plyr library."
  (:use [incanter.core :only [$data col-names conj-rows dataset]])
  (:require [split-apply-combine.core :as sac]))

(defn fast-conj-rows 
  "A simple version of conj-rows that runs much faster"
  [& datasets]
  (when (seq datasets) 
    (dataset (col-names (first datasets))
             (mapcat :rows datasets))))

(defn expr-to-fn
  [expr]
  (let [row-param (gensym "row-")
        kw-map (sac/build-keyword-map expr)]
    `(fn [~row-param]
       (let [~@(apply concat
                      (for [[kw sym] kw-map] 
                        [sym `(get ~row-param ~kw ~kw)]))]
         ~(sac/convert-keywords expr kw-map)))))

(defn exprs-to-fns 
  [group-by]
  (if (coll? group-by)
    (vec (for [item group-by]
           (if (and (coll? item)
                    (coll? (second item))
                    (not (#{'fn 'fn*} (first (second item)))))
             [(first item) (expr-to-fn (second item))]
             item)))
    group-by))

(defn split-ds 
  "Perform a split operation on data, which must be a dataset, using the group-by-fns 
   to choose bins. group-by-fns can either be a single function or a collection of
   functions. In the latter case, the results will be combined to create a key for
   the bin. Returns a map of the group-by-fns results to datasets including all 
   the rows that had the given result.

   Note that keyword column names are the most common functions to use for the 
   group-by."
  [group-by-fns data]
  (let [cols (col-names data)
        group-by-fn (if (= 1 (count group-by-fns))
                      (first group-by-fns) 
                      (apply juxt group-by-fns))]
    (loop [cur (:rows data) row-groups {}]
      (if (empty? cur)
        (for [[group rows] row-groups] [group (dataset cols rows)])
        (recur (next cur)
               (let [row (first cur)
                     k (group-by-fn row)
                     a (row-groups k)]
                 (assoc row-groups k (if a (conj a row) [row]))))))))

(defn apply-ds 
  "Apply fun to each group in grouped-data returning a sequence of pairs of the
   original group-keys and the result of applying the function the dataset. See
   split-ds for information on the grouped-data data structure."
  [fun grouped-data] 
  (for [[group split-data] grouped-data] 
    [group (fun split-data)]))


(defn combine-ds 
  "Combine the datasets in grouped-data into a single dataset including the 
   columns specified in the group-by argument as having the values found in
   the keys in the grouped data.

   If there are columns that are in both the key and the dataset, the values
   in the key have precedence."
  [group-by grouped-data]
  (let [group-by (if (coll? group-by) group-by [group-by])
        group-by-filter (complement (set group-by))] 
    (apply fast-conj-rows 
           (for [[group data] grouped-data]
             (let [grouped-cols (zipmap group-by group)
                   union-cols (concat group-by (filter group-by-filter (col-names data)))]
                (dataset union-cols (map #(merge % grouped-cols) (:rows data))))))))

(defn ddply*
  "Split-apply-combine from datasets to datasets.

   Splits data into a the group of datasets as specified by the group-by argument,
   applies fun to each of the resulting datasets and combines the result of that
   back into a single dataset.

   The group-by argument can be a keyword or collection of keywords which specify
   the columns to group by. It can also include pairs [keyword keyfn] where the 
   function keyfun is applied to each row to generate the key for that row. When
   the groups are combined, keyword is used as the column name for the resulting
   column. The two types of group-by specifications can be mixed.

   The result of the apply function can contain the same columns names as the 
   original dataset or different ones. It can contain the same number of rows as
   the original, a different number, or a single row.

   If data is not specified, it defaults to the currently bound value of $data.

   Examples:

   (ddply* :Symbol
            (transform :Change = (diff0 :Close))
            stock-data)

   (ddply* [[:Month #((juxt year month) (:timestamp %)]]
           (colwise :Volume sum)
           stock-data)"

  ([group-by fun]
     (ddply* group-by fun $data))
  ([group-by fun data]
     (let [group-by (if (coll? group-by) group-by [group-by])
           group-by (for [item group-by]
                      (if (coll? item) item [item item]))]
       (->> data
           (split-ds (map second group-by))
           (apply-ds fun)
           (combine-ds (map first group-by))))))

(defmacro ddply
  "Split-apply-combine from datasets to datasets. This macro is a wrapper on ddply*
   which provides translation of simple column-referencing expressions in the group-by
   argument.

   Splits data into a the group of datasets as specified by the group-by argument,
   applies fun to each of the resulting datasets and combines the result of that
   back into a single dataset.

   The group-by argument can be a keyword or collection of keywords which specify
   the columns to group by. It can also include pairs [keyword key-expr] where the 
   exression key-expr is tranformed to a function and in expr are expanded to accessors
   on rows. The resulting function is applied to each row to generate the key for 
   that row. When the groups are combined, keyword is used as the column name for 
   the resulting column. The two types of group-by specifications can be mixed.

   The result of the apply function can contain the same columns names as the 
   original dataset or different ones. It can contain the same number of rows as
   the original, a different number, or a single row.

   If data is not specified, it defaults to the currently bound value of $data.

   Examples:

   (ddply :Symbol 
          (transform :Change = (diff0 :Close)) 
          stock-data)

   (ddply [[:Month ((juxt year month) :timestamp]]]
          (colwise :Volume sum)
          stock-data)"
  ([group-by fun]
     `(ddply* ~(exprs-to-fns group-by) ~fun $data))
  ([group-by fun data]
     `(ddply* ~(exprs-to-fns group-by) ~fun ~data)))

(defn d_ply*
  "Split-apply-combine from datasets to nothing. This version ignores the output of
   fun and is used for fun's side effects. 

   Splits data into a the group of datasets as specified by the group-by argument,
   applies fun to each of the resulting datasets and then drops the result.

   The group-by argument can be a keyword or collection of keywords which specify
   the columns to group by. It can also include pairs [keyword keyfn] where the 
   function keyfun is applied to each row to generate the key for that row. When
   the groups are combined, keyword is used as the column name for the resulting
   column. The two types of group-by specifications can be mixed.

   The result of the apply function can contain the same columns names as the 
   original dataset or different ones. It can contain the same number of rows as
   the original, a different number, or a single row.

   If data is not specified, it defaults to the currently bound value of $data.

   Example:

   (d_ply* :Symbol 
           #(view (bar-chart :Date :Volume :data %)) 
           stock-data)"
  ([group-by fun]
     (ddply* group-by fun $data))
  ([group-by fun data]
     (let [group-by (if (coll? group-by) group-by [group-by])
           group-by (for [item group-by]
                      (if (coll? item) item [item item]))]
       (dorun
        (->> data
             (split-ds (map second group-by))
             (apply-ds fun))))))

(defmacro d_ply
  "Split-apply-combine from datasets to nothing. This version ignores the output of
   fun and is used for fun's side effects. This macro is a wrapper on d_ply*
   which provides translation of simple column-referencing expressions in the group-by
   argument.

   Splits data into a the group of datasets as specified by the group-by argument,
   applies fun to each of the resulting datasets and then drops the result.

   The group-by argument can be a keyword or collection of keywords which specify
   the columns to group by. It can also include pairs [keyword keyfn] where the 
   function keyfun is applied to each row to generate the key for that row. When
   the groups are combined, keyword is used as the column name for the resulting
   column. The two types of group-by specifications can be mixed.

   The result of the apply function can contain the same columns names as the 
   original dataset or different ones. It can contain the same number of rows as
   the original, a different number, or a single row.

   If data is not specified, it defaults to the currently bound value of $data.

   Example:

   (d_ply :Symbol 
          #(view (bar-chart :Date :Volume :data %)) 
          stock-data)"
  ([group-by fun]
     `(d_ply* ~(exprs-to-fns group-by) ~fun $data))
  ([group-by fun data]
     `(d_ply* ~(exprs-to-fns group-by) ~fun ~data)))
