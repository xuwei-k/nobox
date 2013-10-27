package nobox
package benchmark

import org.scalameter.api._

object ExistsBenchmark extends NoBoxBenchmark {
  simpleComparison("exists")(_.exists(_ == -1), _.exists(_ == -1))
}
