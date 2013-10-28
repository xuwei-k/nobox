package nobox
package benchmark

import org.scalameter.api._

object ExistsBenchmark extends NoBoxBenchmark {
  simpleIntComparison("exists")(_.exists(_ == -1), _.exists(_ == -1))
  simpleLongComparison("exists")(_.exists(_ == -1L), _.exists(_ == -1L))
}
