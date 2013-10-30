package nobox
package benchmark

import org.scalameter.api._

object ContainsBenchmark extends NoBoxBenchmark {
  simpleIntComparison("contains")(_.contains(-1), _.contains(-1))
  simpleLongComparison("contains")(_.contains(-1L), _.contains(-1L))
}
