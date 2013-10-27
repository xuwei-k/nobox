package nobox
package benchmark

import org.scalameter.api._

object ContainsBenchmark extends NoBoxBenchmark {
  simpleComparison("contains")(_.contains(-1), _.contains(-1))
}
