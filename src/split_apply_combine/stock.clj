(ns split-apply-combine.stock
  "Code for manipulating stock tables pulled from yahoo finance."
  (:use [incanter.core :only [$ dataset conj-rows]]) 
  (:require [clojure.pprint :as pp]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [incanter.io :as io]
            [split-apply-combine.core :as sac]))

(def ^:private yahoo-url-format "http://ichart.finance.yahoo.com/table.csv?s=~a&a=~d&b=~d&c=~d&d=~d&e=~d&f=~d&g=d&ignore=.csv")

(defn ^:private url-for 
  "Build the Yahoo finance URL for one symbol in the given date range. The dates should be Joda DateTime objects."
  [sym start-date end-date]
  (pp/cl-format nil yahoo-url-format 
                sym
                (dec (time/month start-date)) (time/day start-date) (time/year start-date)
                (dec (time/month end-date)) (time/day end-date) (time/year end-date)))

(defn load-yahoo-data 
  "Return a dataset of the yahoo finance data for market activity for the given ticker symbols 
 (which can be a string or seqable) in the date range (specified as YYYY-MM-DD)"
  [syms start-date end-date]
  (let [start-date (time-format/parse (time-format/formatters :year-month-day) start-date)
        end-date (time-format/parse (time-format/formatters :year-month-day) end-date)
        syms (if (string? syms) [syms] syms)]
    (reduce conj-rows 
            (for [sym syms] (sac/add-identifier 
                             :Symbol sym 
                             (io/read-dataset (url-for sym start-date end-date) :header true))))))

(defn read-saved-data
  "Read the sample stock data I pulled from yahoo and stashed in the committed file"
  []
  (io/read-dataset "data/tech-stocks.csv" :header true))

(defn add-changes
  "Add the day to day change in close price and the ratio of today's close to the first close"
  (ply/ddply 
   :Symbol 
   (sac/transform 
    :Change (sac/diff0 :Close) 
    :Relative (map #(/ % (first :Close)) :Close)) 
   tech-stocks))
