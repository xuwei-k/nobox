# nobox [![Build Status](https://secure.travis-ci.org/xuwei-k/nobox.png?branch=master)](http://travis-ci.org/xuwei-k/nobox)

immutable primitive array wrapper for Scala

https://bintray.com/xuwei-k/maven/nobox

```scala
resolvers += "bintray" at "http://dl.bintray.com/xuwei-k/maven"

libraryDependencies += "com.github.xuwei-k" %% "nobox" % "0.1.2"
```

## what's this

nobox means **No** **Box**ing primitive values.
There are [`ArrayOps`](https://github.com/scala/scala/blob/v2.10.3/src/library/scala/collection/mutable/ArrayOps.scala) and [`WrappedArray`](https://github.com/scala/scala/blob/v2.10.3/src/library/scala/collection/mutable/WrappedArray.scala) in [Scala standard library](http://docs.scala-lang.org/overviews/collections/arrays.html), but these operations sometimes so slow.
This library provide more efficient(faster and less memory) operations for primitive arrays.
