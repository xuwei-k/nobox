package nobox
package benchmark

import org.scalameter.api._

object ExistsBenchmark extends NoboxBenchmark {
  performance of "scala.Array[Int]" in {
    measure method "exists" in {
      using(arInts) curve("exists") in {
        a ⇒ a.exists(_ == -1)
      }
    }
  }

  performance of "nobox.ofInt" in {
    measure method "exists" in {
      using(ofInts) curve("exists") in {
        a ⇒ a.exists(_ == -1)
      }
    }
  }
}
