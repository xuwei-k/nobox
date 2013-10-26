package nobox
package benchmark

import org.scalameter.api._

object MapBenchmark extends NoboxBenchmark {
  performance of "scala.Array[Int]" in {
    measure method "map" in {
      using(arInts) curve("map") in {
        a ⇒ a.map(_ + 1)
      }
    }
  }

  performance of "nobox.ofInt" in {
    measure method "mapInt" in {
      using(ofInts) curve("map") in {
        a ⇒ a.mapInt(_ + 1)
      }
    }
  }
}
