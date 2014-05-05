package nobox
package benchmark

object MapBenchmark extends NoBoxBenchmark {
  simpleIntComparison("map")(_.map(_ + 1), _.mapInt(_ + 1))
  simpleLongComparison("map")(_.map(_ + 1L), _.mapLong(_ + 1L))
}
