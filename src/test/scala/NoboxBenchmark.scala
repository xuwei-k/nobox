package nobox
package benchmark

import org.scalameter.api._

trait NoboxBenchmark extends PerformanceTest {
  lazy val executor = LocalExecutor(new Executor.Warmer.Default, Aggregator.min, new Measurer.Default)
  lazy val reporter = new LoggingReporter
//  lazy val reporter = ChartReporter(ChartFactory.XYLine())
  lazy val persistor = Persistor.None

  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val arInts = for (size ← sizes) yield util.Random.shuffle(1 to size).toArray
  val ofInts = for (array ← arInts) yield new ofInt(array)
}
