# split-apply-combine

This is a sample implementation of ddply and some other R type stuff that I did for my June 2013 presentation to the Bay Area Clojure Users Group.

This is not meant to be a complete implementation, but rather as a demonstration of the Split-Apply-Combine concepts proposed by Hadley Wickham in his paper _The Split-Apply-Combine Strategy for Data Analysis_ (Journal of Statistical Software, April 2011, Volume 40, Issue 1) and implemented in the plyr library. See [http://plyr.had.co.nz](http://plyr.had.co.nz) for more information on the plyr project.

There is API documentation for these functions at [http://tomfaulhaber.github.io/split-apply-combine](http://tomfaulhaber.github.io/split-apply-combine).

## Components 

These are the files and what's in each of them:

In the root directory:

* __script.clj__ has the set of commands that I ran interactively to construct the results we saw during the live demo. I also used `nrow`, `head`, and `frequencies` to do some _ad hoc_ exploration of the data along the way.

In `src/split-apply-combine`:

* __core.clj__ has `transform`, `transform*`, and `colwise` with their supporting functions. There are also a few other generally useful functions.
* __ply.clj__ has the implementation of `ddply`, `ddply*`, `d_ply`, and `d_ply*` and their various supporting functions.
* __cpu.clj__ has routines for defining, loading an manipulating the CPU load data I showed during the demo. (Note the only part of this we used during the demo was the `cpu-files` data which had the list of data files.)
* __stock.clj__ has routines for loading and manipulating the stock data I spoke about. This data was all pulled from yahoo finance.

Data files (in `data/`):

* __tech-stocks.csv__ is some simple stock data that include the data on the slides (Amazon, IBM, and Microsoft from the first four months of 2013). Read it with the `read-saved-data` function. It's easy to pull your own data from yahoo finance, just use the `load-yahoo-data` function.
* __scdb\_agent\_*.cpu__ is the set of files with CPU load data that we used during the demo portion of the presentation. Thanks to SpaceCurve, Inc. ([www.spacecurve.com](www.spacecurve.com)) for permission to use this data.

## License

Copyright © 2013 Tom Faulhaber

Distributed under the Eclipse Public License, the same as Clojure.
