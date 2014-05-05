package nobox
package benchmark

object ExistsBenchmark extends NoBoxBenchmark {
  simpleIntComparison("exists")(_.exists(_ == -1), _.exists(_ == -1))
  simpleLongComparison("exists")(_.exists(_ == -1L), _.exists(_ == -1L))
}
