(ns split-apply-combine.ply
  (:use [incanter.core :only [$data col-names conj-rows dataset]])
  (:require [split-apply-combine.core :as sac]))

(defn- map-get
  ([m k]
     (if (keyword? k)
       (or (get m k) (get m (name k)))
       (get m k)))
  ([m k colnames]
     (cond
      (keyword? k)
        (or (get m k) (get m (name k)))
      (number? k)
        (get m (nth colnames k))
      :else
        (get m k))))

(defn- submap [m ks]
  (zipmap (if (coll? ks) ks [ks])
          (map #(map-get m %) (if (coll? ks) ks [ks]))))

(defn split-ds 
  "WRITE A DOC STRING"
  [group-by data]
   (let [cols (col-names data)]
     (loop [cur (:rows data) row-groups {}]
       (if (empty? cur)
         (for [[group rows] row-groups] [group (dataset cols rows)])
         (recur (next cur)
                (let [row (first cur)
                      k (submap row group-by)
                      a (row-groups k)]
                  (assoc row-groups k (if a (conj a row) [row]))))))))

(defn apply-ds [fun grouped-data] 
  (for [[group split-data] grouped-data] 
    [group (fun split-data)]))

(defn combine-ds [group-by grouped-data]
  (let [group-by (if (coll? group-by) group-by [group-by])
        group-by-filter (complement (set group-by))] 
    (reduce conj-rows 
            (for [[group data] grouped-data]
              (let [union-cols (concat group-by (filter group-by-filter (col-names data)))]
                (dataset union-cols (map #(merge % group) (:rows data))))))))

(defn ddply
"WRITE A DOC STRING"
  ([group-by fun]
     (ddply group-by fun $data))
  ([group-by fun data]
     (->> data
          (split-ds group-by)
          (apply-ds fun)
          (combine-ds group-by))))
