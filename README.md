# nobox

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.xuwei-k/nobox_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.xuwei-k/nobox_2.12)
[![scaladoc](https://javadoc-badge.appspot.com/com.github.xuwei-k/nobox_2.12.svg?label=javadoc)](https://javadoc-badge.appspot.com/com.github.xuwei-k/nobox_2.12/nobox/index.html?javadocio=true)


immutable primitive array wrapper for Scala

## what's this

nobox means **No** Boxing primitive values.
There are [`ArrayOps`](https://github.com/scala/scala/blob/v2.12.13/src/library/scala/collection/mutable/ArrayOps.scala) and [`WrappedArray`](https://github.com/scala/scala/blob/v2.12.13/src/library/scala/collection/mutable/WrappedArray.scala) in [Scala standard library](http://docs.scala-lang.org/overviews/collections/arrays.html), but these operations sometimes so slow.
This library provide more efficient(faster and less memory) operations for primitive arrays.

### latest stable version

```scala
libraryDependencies += "com.github.xuwei-k" %% "nobox" % "0.3.1"
```

for scala-js, scala-native

```scala
libraryDependencies += "com.github.xuwei-k" %%% "nobox" % "0.3.1"
```

- [generated source code](http://java-src.appspot.com/com.github.xuwei-k/nobox_2.12?latest)
