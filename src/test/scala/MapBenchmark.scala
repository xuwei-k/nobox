package nobox
package benchmark

import org.scalameter.api._

object MapBenchmark extends NoBoxBenchmark {
  simpleComparison("map")(_.map(_ + 1), _.mapInt(_ + 1))
}
