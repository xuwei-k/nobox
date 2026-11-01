package nobox

import scalaprops.Property.forAll

object TestInt1 extends TestBase {

  property("head") = forAll { (a: ofInt1) =>
    a.head must_== a.self.head
  }

  property("tail") = forAll { (a: ofInt1) =>
    a.tail.self must_=== a.self.tail
  }

  property("exists") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.exists(f) must_== a.self.exists(f)
  }

  property("filter") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  property("find") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.find(f) must_== a.self.find(f)
  }

  property("forall") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.forall(f) must_== a.self.forall(f)
  }

  property("reverse.reverse") = forAll { (a: ofInt1) =>
    a.reverse.reverse === a
  }

  property("reverse") = forAll { (a: ofInt1) =>
    a.reverse.self must_=== a.self.reverse
  }

  property("take") = forAll { (a: ofInt1, n: Int) =>
    a.take(n).self must_=== a.self.take(n)
  }

  property("takeWhile") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  property("count") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.count(f) must_== a.self.count(f)
  }

  property("toList") = forAll { (a: ofInt1) =>
    a.toList must_== a.self.toList
  }

  property("toArray") = forAll { (xs: ofInt1) =>
    xs.toArray must_=== xs.self.toArray
  }

  property("drop") = forAll { (a: ofInt1, n: Int) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  property("dropWhile") = forAll { (a: ofInt1, f: Int => Boolean) =>
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  property("dropRight") = forAll { (a: ofInt1, n: Int) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  property("contains") = forAll { (a: ofInt1, n: Int) =>
    a.contains(n) must_== a.self.contains(n)
  }

  property("++") = forAll { (a: ofInt1, b: ofInt1) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  property("sorted") = forAll { (a: ofInt1) =>
    a.sorted.self must_=== a.self.sorted
  }

  property("reduceLeft") = forAll { (a: ofInt1) =>
    a.reduceLeft(_ - _) must_== a.self.reduceLeftOption(_ - _).get
  }

  property("reduceRightOption") = forAll { (a: ofInt1) =>
    a.reduceRight(_ - _) must_== a.self.reduceRightOption(_ - _).get
  }

  property("indexOf") = forAll { (a: ofInt1, z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  property("lastIndexOf") = forAll { (a: ofInt1, z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  property("mkString") = forAll { (a: ofInt1, start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  property("maxBy") = forAll { (a: ofInt1) =>
    val f = (_: Int).toHexString
    a.maxBy(f) must_== a.self.maxBy(f)
  }

  property("minBy") = forAll { (a: ofInt1) =>
    val f = (_: Int).toHexString
    a.minBy(f) must_== a.self.minBy(f)
  }

  property("max") = forAll { (a: ofInt1) =>
    a.max must_== a.self.max
  }

  property("min") = forAll { (a: ofInt1) =>
    a.min must_== a.self.min
  }

  property("minmax") = forAll { (a: ofInt1) =>
    a.minmax must_== (a.self.min -> a.self.max)
  }

  property("scanLeft1") = forAll { (a: ofInt1, f: (Int, Int) => Int) =>
    a.scanLeft1(f).self.toList must_== a.self.tail.scanLeft(a.self.head)(f).toList
  }

  property("scanRight1") = forAll { (a: ofInt1) =>
    a.scanRight1(_ - _).self.toList must_== a.self.init.scanRight(a.self.last)(_ - _).toList
  }

  property("toString") = forAll { (xs: ofInt1) =>
    xs.toString must_== xs.mkString("ofInt1(", ", ", ")")
  }

}
