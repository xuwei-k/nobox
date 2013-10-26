package nobox
package benchmark

import org.scalameter.api._

object ContainsBenchmark extends NoboxBenchmark {
  performance of "scala.Array[Int]" in {
    measure method "contains" in {
      using(arInts) curve("contains") in {
        a ⇒ a.contains(-1)
      }
    }
  }

  performance of "nobox.ofInt" in {
    measure method "contains" in {
      using(ofInts) curve("contains") in {
        a ⇒ a.contains(-1)
      }
    }
  }
}
