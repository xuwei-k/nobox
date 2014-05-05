package nobox
package benchmark

object ReverseMapBenchmark extends NoBoxBenchmark {
  simpleIntComparison("reverse map")(_.reverse.map(_ + 1), _.reverseMapInt(_ + 1))
  simpleLongComparison("reverse map")(_.reverse.map(_ + 1L), _.reverseMapLong(_ + 1L))
}
