package nobox

import scalaprops.Property.forAll

object TestInt1 extends TestBase {

  val head = forAll { (a: ofInt1) =>
    a.head must_== a.self.head
  }

  val tail = forAll { (a: ofInt1) =>
    a.tail.self must_=== a.self.tail
  }

  val exists = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.exists(f) must_== a.self.exists(f)
  }

  val filter = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  val find = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.find(f) must_== a.self.find(f)
  }

  val forall = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.forall(f) must_== a.self.forall(f)
  }

  val `reverse.reverse` = forAll { (a: ofInt1) =>
    a.reverse.reverse === a
  }

  val reverse = forAll { (a: ofInt1) =>
    a.reverse.self must_=== a.self.reverse
  }

  val take = forAll { (a: ofInt1, n: Int) =>
    a.take(n).self must_=== a.self.take(n)
  }

  val takeWhile = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  val count = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.count(f) must_== a.self.count(f)
  }

  val toList = forAll { (a: ofInt1) =>
    a.toList must_== a.self.toList
  }

  val toArray = forAll { (xs: ofInt1) =>
    xs.toArray must_=== xs.self.toArray
  }

  val drop = forAll { (a: ofInt1, n: Int) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  val dropWhile = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  val dropRight = forAll { (a: ofInt1, n: Int) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  val contains = forAll { (a: ofInt1, n: Int) =>
    a.contains(n) must_== a.self.contains(n)
  }

  val ++ = forAll { (a: ofInt1, b: ofInt1) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  val sorted = forAll { (a: ofInt1) =>
    a.sorted.self must_=== a.self.sorted
  }

  val reduceLeft = forAll { (a: ofInt1) =>
    a.reduceLeft(_ - _) must_== a.self.reduceLeftOption(_ - _).get
  }

  val reduceRightOption = forAll { (a: ofInt1) =>
    a.reduceRight(_ - _) must_== a.self.reduceRightOption(_ - _).get
  }

  val indexOf = forAll { (a: ofInt1, z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  val lastIndexOf = forAll { (a: ofInt1, z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  val mkString = forAll { (a: ofInt1, start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  val maxBy = forAll { (a: ofInt1) =>
    val f = (_: Int).toHexString
    a.maxBy(f) must_== a.self.maxBy(f)
  }

  val minBy = forAll { (a: ofInt1) =>
    val f = (_: Int).toHexString
    a.minBy(f) must_== a.self.minBy(f)
  }

  val max = forAll { (a: ofInt1) =>
    a.max must_== a.self.max
  }

  val min = forAll { (a: ofInt1) =>
    a.min must_== a.self.min
  }

  val minmax = forAll { (a: ofInt1) =>
    a.minmax must_== (a.self.min -> a.self.max)
  }

  val scanLeft1 = forAll { (a: ofInt1, f: (Int, Int) => Int) =>
    a.scanLeft1(f).self.toList must_== a.self.tail.scanLeft(a.self.head)(f).toList
  }

  val scanRight1 = forAll { (a: ofInt1) =>
    a.scanRight1(_ - _).self.toList must_== a.self.init.scanRight(a.self.last)(_ - _).toList
  }

  val testToString = forAll { (xs: ofInt1) =>
    xs.toString must_== xs.mkString("ofInt1(", ", ", ")")
  }

}
