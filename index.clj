{:namespaces
 ({:source-url nil,
   :wiki-url "split-apply-combine.core-api.html",
   :name "split-apply-combine.core",
   :doc
   "The general functions for working with data using the split apply combine model."}
  {:source-url nil,
   :wiki-url "split-apply-combine.cpu-api.html",
   :name "split-apply-combine.cpu",
   :doc
   "Routines to manipulate CPU load data from a 38 minute test in a small cluster.\nThanks to SpaceCurve, Inc. (http://www.spacecurve.com) for permission to use \nthis data."}
  {:source-url nil,
   :wiki-url "split-apply-combine.ply-api.html",
   :name "split-apply-combine.ply",
   :doc
   "Implementation of the split-apply-combine functions, similar to R's plyr library."}
  {:source-url nil,
   :wiki-url "split-apply-combine.stock-api.html",
   :name "split-apply-combine.stock",
   :doc
   "Code for manipulating stock tables pulled from yahoo finance."}),
 :vars
 ({:arglists ([col-name val data]),
   :name "add-identifier",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/add-identifier",
   :doc "Add a constant identifier column to the dataset",
   :var-type "function",
   :line 64,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([expr]),
   :name "build-keyword-map",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/build-keyword-map",
   :doc
   "Find all the unique keywords in expr and make a gensymed symbol for each. Return \na map of the keywords to their corresponding symbols. This is used as a support \nfunction in the transform macro.",
   :var-type "function",
   :line 95,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([op expr]),
   :name "build-transform-fn",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/build-transform-fn",
   :doc
   "Builds a function for expr depending on op. The function will take a dataset\nand return the value of expr. Keywords will be interpreted as shorthand for the\ncorresponding column in the dataset when there is one. Evaluation of the columns\nis factored out, so that it only happpens once no matter how many times the \nkeyword is referenced.\n\nThis is a support function for the transform macro. The the documentation there \nfor the information on the available values for op.",
   :var-type "function",
   :line 111,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([kw map? data]),
   :name "col-or-keyword",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/col-or-keyword",
   :doc
   "If kw is the name of a column in data, return that column. Otherwise\njust return kw itself. If map is true, we return an seq of keywords \nas long as their are rows in the data.",
   :var-type "function",
   :line 86,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([cols f]),
   :name "colwise",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/colwise",
   :doc
   "Returns a function that applies f to each of the columns in cols and\nproduces a new dataset with only the modified columns",
   :var-type "function",
   :line 49,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([expr kw-map]),
   :name "convert-keywords",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/convert-keywords",
   :doc
   "Walk expr and change all the keywords into the symbol specified for that keyword\nin the kw-map. This is a support function for the transform macro.",
   :var-type "function",
   :line 103,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([coll]),
   :name "diff",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/diff",
   :doc
   "Takes a seqable (coll) and returns a seq of the differences between each element.\nThe resulting seq is one shorter than the input",
   :var-type "function",
   :line 11,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([coll]),
   :name "diff0",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/diff0",
   :doc
   "Like diff, but adds a zero as the first element in order to \nhave the result be the same length as coll",
   :var-type "function",
   :line 21,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([] [data]),
   :name "nrow",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/nrow",
   :doc "Return the number of rows in a dataset",
   :var-type "function",
   :line 34,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([data]),
   :name "numeric-cols",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/numeric-cols",
   :doc
   "Return the names of the numeric columns from a dataset. A column is considered\nnumeric if its first row is numeric.",
   :var-type "function",
   :line 39,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([data]),
   :name "pr-data",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/pr-data",
   :doc "Use print-table to print a dataset in a nice tabular format",
   :var-type "function",
   :line 71,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([& transforms]),
   :name "transform",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/transform",
   :doc
   "Returns a function that transforms a dataset by changing or adding columns.\n\nEach transform is expressed as a triple:\n    column-name operator transform-expr\n\nThere are two operators available:\n    =    Evaluates the expression on the whole dataset and return a result \n         for the specified column.\n    =*   Evaluates the expression for each row of the row of the dataset and \n         return a result for that row in the specified column (that is, \n         perform an implicit map).\n\nWithin each transform-expr, keywords will be interpreted as referring to the\nnamed column in the input dataset. The implicit sel operation is performed\nexactly once for each function invocation regardless of how many times the\nkeyword is used in the transform-expr.\n\nIf the column name was part of the original dataset, it is replaced in the output. \nOtherwise, it is added to the output.\n\nExample:\n    ((transform\n       :Average  =* (/ (+ :Open :Close) 2)\n       :Change   = (diff0 :Close) \n       :Relative = (map #(/ % (first :Close)) :Close)\n     stock-data)\n\nThe transform macro is a wrapper around the transform* function. Sometimes, using\ntransform* directly is a better choice.",
   :var-type "macro",
   :line 164,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([& transforms]),
   :name "transform*",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/transform*",
   :doc
   "Returns a function that transforms a dataset by changing or adding columns. \n\nThe transforms are in pairs with a column name and a transform function\nthat will produce a the new value for that column. The transform functions will\nbe passed a single argument, the dataset that transform* was called with.\n\nIf the column name was part of the original dataset, it is replaced in the output. \nOtherwise, it is added to the output.\n\nExample: ((transform* :Change #(sac/diff0 ($ :Close %)) \n                      :Relative #(let [close ($ :Close %)] \n                                   (map (fn [val] (/ val (first close))) close)))\n           stock-data)\n\nFor a version that allows for terser expression of the transforms, see the\ntransform macro.",
   :var-type "function",
   :line 137,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([coll] [coll sorted?]),
   :name "unique",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/unique",
   :doc "Returns unique values of a coll, sorted by default",
   :var-type "function",
   :line 27,
   :file "src/split_apply_combine/core.clj"}
  {:arglists ([file-name data]),
   :name "write-dataset-csv",
   :namespace "split-apply-combine.core",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.core-api.html#split-apply-combine.core/write-dataset-csv",
   :doc "Writes a data set as a CSV file, with headers",
   :var-type "function",
   :line 78,
   :file "src/split_apply_combine/core.clj"}
  {:file "src/split_apply_combine/cpu.clj",
   :raw-source-url nil,
   :source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/cpu-files",
   :namespace "split-apply-combine.cpu",
   :line 14,
   :var-type "var",
   :doc
   "The list of IP address, file name pairs that we used for our CPU load demos.",
   :name "cpu-files"}
  {:arglists ([cpu data]),
   :name "data-by-cpu",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/data-by-cpu",
   :doc
   "Take raw data read from a single file and get the data for a single core, as \ndifferences between samples.",
   :var-type "function",
   :line 90,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([cpu-data]),
   :name "load-average",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/load-average",
   :doc
   "Reduce each normalized CPU measurement across cores to compute a real load average for each machine.",
   :var-type "function",
   :line 54,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([cpu data]),
   :name "load-chart",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/load-chart",
   :doc "Do a simple load chart for a single core.",
   :var-type "function",
   :line 97,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([]),
   :name "load-cpu-data",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/load-cpu-data",
   :doc
   "Load the CPU data from each of the files specified in cpu-files. \nWe normalize it slightly by adding a column with the IP address,\nconverting the times to JODA DateTimes and turning the core ids\ninto keywords.",
   :var-type "function",
   :line 23,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([cpu-data]),
   :name "normalize-cpu-data",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/normalize-cpu-data",
   :doc
   "Normalize CPU results by % within the interval and add a load factor",
   :var-type "function",
   :line 40,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([]),
   :name "read-to-render",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/read-to-render",
   :doc
   "Read the data from the files listed in cpu-data and render mean load by minute.",
   :var-type "function",
   :line 81,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([data]),
   :name "render-graphs",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/render-graphs",
   :doc "Draw a load graph for each node represented in data",
   :var-type "function",
   :line 71,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([data]),
   :name "rollup-to-minutes",
   :namespace "split-apply-combine.cpu",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.cpu-api.html#split-apply-combine.cpu/rollup-to-minutes",
   :doc
   "Divide the data by IP and take the mean load for each minute's data",
   :var-type "function",
   :line 60,
   :file "src/split_apply_combine/cpu.clj"}
  {:arglists ([fun grouped-data]),
   :name "apply-ds",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/apply-ds",
   :doc
   "Apply fun to each group in grouped-data returning a sequence of pairs of the\noriginal group-keys and the result of applying the function the dataset. See\nsplit-ds for information on the grouped-data data structure.",
   :var-type "function",
   :line 57,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by grouped-data]),
   :name "combine-ds",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/combine-ds",
   :doc
   "Combine the datasets in grouped-data into a single dataset including the \ncolumns specified in the group-by argument as having the values found in\nthe keys in the grouped data.\n\nIf there are columns that are in both the key and the dataset, the values\nin the key have precedence.",
   :var-type "function",
   :line 66,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by fun] [group-by fun data]),
   :name "d_ply",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/d_ply",
   :doc
   "Split-apply-combine from datasets to nothing. This version ignores the output of\nfun and is used for fun's side effects. This macro is a wrapper on d_ply*\nwhich provides translation of simple column-referencing expressions in the group-by\nargument.\n\nSplits data into a the group of datasets as specified by the group-by argument,\napplies fun to each of the resulting datasets and then drops the result.\n\nThe group-by argument can be a keyword or collection of keywords which specify\nthe columns to group by. It can also include pairs [keyword keyfn] where the \nfunction keyfun is applied to each row to generate the key for that row. When\nthe groups are combined, keyword is used as the column name for the resulting\ncolumn. The two types of group-by specifications can be mixed.\n\nThe result of the apply function can contain the same columns names as the \noriginal dataset or different ones. It can contain the same number of rows as\nthe original, a different number, or a single row.\n\nIf data is not specified, it defaults to the currently bound value of $data.\n\nExample:\n\n(d_ply :Symbol \n       #(view (bar-chart :Date :Volume :data %)) \n       stock-data)",
   :var-type "macro",
   :line 193,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by fun] [group-by fun data]),
   :name "d_ply*",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/d_ply*",
   :doc
   "Split-apply-combine from datasets to nothing. This version ignores the output of\nfun and is used for fun's side effects. \n\nSplits data into a the group of datasets as specified by the group-by argument,\napplies fun to each of the resulting datasets and then drops the result.\n\nThe group-by argument can be a keyword or collection of keywords which specify\nthe columns to group by. It can also include pairs [keyword keyfn] where the \nfunction keyfun is applied to each row to generate the key for that row. When\nthe groups are combined, keyword is used as the column name for the resulting\ncolumn. The two types of group-by specifications can be mixed.\n\nThe result of the apply function can contain the same columns names as the \noriginal dataset or different ones. It can contain the same number of rows as\nthe original, a different number, or a single row.\n\nIf data is not specified, it defaults to the currently bound value of $data.\n\nExample:\n\n(d_ply* :Symbol \n        #(view (bar-chart :Date :Volume :data %)) \n        stock-data)",
   :var-type "function",
   :line 158,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by fun] [group-by fun data]),
   :name "ddply",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/ddply",
   :doc
   "Split-apply-combine from datasets to datasets. This macro is a wrapper on ddply*\nwhich provides translation of simple column-referencing expressions in the group-by\nargument.\n\nSplits data into a the group of datasets as specified by the group-by argument,\napplies fun to each of the resulting datasets and combines the result of that\nback into a single dataset.\n\nThe group-by argument can be a keyword or collection of keywords which specify\nthe columns to group by. It can also include pairs [keyword key-expr] where the \nexression key-expr is tranformed to a function and in expr are expanded to accessors\non rows. The resulting function is applied to each row to generate the key for \nthat row. When the groups are combined, keyword is used as the column name for \nthe resulting column. The two types of group-by specifications can be mixed.\n\nThe result of the apply function can contain the same columns names as the \noriginal dataset or different ones. It can contain the same number of rows as\nthe original, a different number, or a single row.\n\nIf data is not specified, it defaults to the currently bound value of $data.\n\nExamples:\n\n(ddply :Symbol \n       (transform :Change = (diff0 :Close)) \n       stock-data)\n\n(ddply [[:Month ((juxt year month) :timestamp]]]\n       (colwise :Volume sum)\n       stock-data)",
   :var-type "macro",
   :line 122,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by fun] [group-by fun data]),
   :name "ddply*",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/ddply*",
   :doc
   "Split-apply-combine from datasets to datasets.\n\nSplits data into a the group of datasets as specified by the group-by argument,\napplies fun to each of the resulting datasets and combines the result of that\nback into a single dataset.\n\nThe group-by argument can be a keyword or collection of keywords which specify\nthe columns to group by. It can also include pairs [keyword keyfn] where the \nfunction keyfun is applied to each row to generate the key for that row. When\nthe groups are combined, keyword is used as the column name for the resulting\ncolumn. The two types of group-by specifications can be mixed.\n\nThe result of the apply function can contain the same columns names as the \noriginal dataset or different ones. It can contain the same number of rows as\nthe original, a different number, or a single row.\n\nIf data is not specified, it defaults to the currently bound value of $data.\n\nExamples:\n\n(ddply* :Symbol\n         (transform :Change = (diff0 :Close))\n         stock-data)\n\n(ddply* [[:Month #((juxt year month) (:timestamp %)]]\n        (colwise :Volume sum)\n        stock-data)",
   :var-type "function",
   :line 82,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([& datasets]),
   :name "fast-conj-rows",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/fast-conj-rows",
   :doc "A simple version of conj-rows that runs much faster",
   :var-type "function",
   :line 6,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([group-by-fns data]),
   :name "split-ds",
   :namespace "split-apply-combine.ply",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.ply-api.html#split-apply-combine.ply/split-ds",
   :doc
   "Perform a split operation on data, which must be a dataset, using the group-by-fns \nto choose bins. group-by-fns can either be a single function or a collection of\nfunctions. In the latter case, the results will be combined to create a key for\nthe bin. Returns a map of the group-by-fns results to datasets including all \nthe rows that had the given result.\n\nNote that keyword column names are the most common functions to use for the \ngroup-by.",
   :var-type "function",
   :line 34,
   :file "src/split_apply_combine/ply.clj"}
  {:arglists ([stocks]),
   :name "add-changes",
   :namespace "split-apply-combine.stock",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.stock-api.html#split-apply-combine.stock/add-changes",
   :doc
   "Add the day to day change in close price and the ratio of today's close to the first close",
   :var-type "function",
   :line 38,
   :file "src/split_apply_combine/stock.clj"}
  {:arglists ([syms start-date end-date]),
   :name "load-yahoo-data",
   :namespace "split-apply-combine.stock",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.stock-api.html#split-apply-combine.stock/load-yahoo-data",
   :doc
   "Return a dataset of the yahoo finance data for market activity for the given ticker symbols \n(which can be a string or seqable) in the date range (specified as YYYY-MM-DD)",
   :var-type "function",
   :line 21,
   :file "src/split_apply_combine/stock.clj"}
  {:arglists ([]),
   :name "read-saved-data",
   :namespace "split-apply-combine.stock",
   :source-url nil,
   :raw-source-url nil,
   :wiki-url
   "/split-apply-combine.stock-api.html#split-apply-combine.stock/read-saved-data",
   :doc
   "Read the sample stock data I pulled from yahoo and stashed in the committed file",
   :var-type "function",
   :line 33,
   :file "src/split_apply_combine/stock.clj"})}
