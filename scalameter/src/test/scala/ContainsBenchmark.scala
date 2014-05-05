package nobox
package benchmark

object ContainsBenchmark extends NoBoxBenchmark {
  simpleIntComparison("contains")(_.contains(-1), _.contains(-1))
  simpleLongComparison("contains")(_.contains(-1L), _.contains(-1L))
}
