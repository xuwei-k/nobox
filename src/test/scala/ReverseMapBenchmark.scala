package nobox
package benchmark

import org.scalameter.api._

object ReverseBenchmark extends NoBoxBenchmark {
  performance of "scala.Array[Int]" in {
    measure method "reverse.map" in {
      using(arInts) curve("map") in {
        a ⇒ a.reverse.map(_ + 1)
      }
    }

    measure method "reverseMap" in {
      using(arInts) curve("map") in {
        a ⇒ a.reverseMap(_ + 1)
      }
    }
  }

  performance of "nobox.ofInt" in {
    measure method "reverseMapInt" in {
      using(ofInts) curve("map") in {
        a ⇒ a.reverseMapInt(_ + 1)
      }
    }
  }
}
