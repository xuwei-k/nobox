package nobox
package benchmark

import org.scalameter.api._

object ReverseMapBenchmark extends NoBoxBenchmark {
  simpleComparison("reverse map")(_.reverse.map(_ + 1), _.reverseMapInt(_ + 1))
}
