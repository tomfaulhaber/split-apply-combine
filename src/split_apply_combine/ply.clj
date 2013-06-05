(ns split-apply-combine.ply
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
    (for [item group-by] 
      (if (and (coll? item)
               (coll? (second item))
               (not (#{'fn 'fn*} (first (second item)))))
        [(first item) (expr-to-fn (second item))]
        item))
    group-by))

(defn split-ds 
  ""
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

(defn apply-ds [fun grouped-data] 
  (for [[group split-data] grouped-data] 
    [group (fun split-data)]))


(defn combine-ds [group-by grouped-data]
  (let [group-by (if (coll? group-by) group-by [group-by])
        group-by-filter (complement (set group-by))] 
    (apply fast-conj-rows 
            (for [[group data] grouped-data]
              (let [union-cols (concat group-by (filter group-by-filter (col-names data)))]
                (dataset union-cols (map #(merge % group) (:rows data))))))))

(defn ddply*
"WRITE A DOC STRING"
  ([group-by fun]
     (ddply group-by fun $data))
  ([group-by fun data]
     (let [_ (println group-by) 
           group-by (if (coll? group-by) group-by [group-by]) 
           group-by (for [item group-by]
                      (if (coll? item) item [item item]))
           _ (println group-by)] 
       (->> data
           (split-ds (map second group-by))
           (apply-ds fun)
           (combine-ds (map first group-by))))))

(defmacro ddply
  "WRITE A DOC STRING"
  [group-by fun data]
  `(ddply* '~(exprs-to-fns group-by) ~fun ~data))
