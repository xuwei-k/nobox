package nobox

import org.scalacheck._
import Prop.forAll

object Test extends Properties("nobox"){
  
  implicit class ArrayOps[A](val self: Array[A]) extends AnyVal {
    def ===(that: Array[A]): Boolean = {
      if(self sameElements that) true
      else {
        val msg = self.mkString("Array(",",",")") + " is not equal " + that.mkString("Array(",",",")")
        throw new AssertionError(msg)
      }
    }
  }

  implicit val ofIntArb: Arbitrary[ofInt] =
    Arbitrary(implicitly[Arbitrary[Array[Int]]].arbitrary.map(array => ofInt(array: _*)))

  val pf: PartialFunction[Int, Long] = {case i: Int if i % 3 == 0 => i + 1}
  val f: Int => Boolean = {i: Int => 5 < i && i < 10 }

  property("collect") = forAll { a: ofInt =>
    a.collectLong(pf).self === a.self.collect(pf)
  }

  property("exists") = forAll { a: ofInt =>
    a.exists(f) == a.self.exists(f)
  }

  property("filter") = forAll { a: ofInt =>
    a.filter(f).self === a.self.filter(f)
  }

  property("find") = forAll { a: ofInt =>
    a.find(f) == a.self.find(f)
  }

  property("flatMap") = forAll { a: ofInt =>
    val f: Int => Array[Int] = {i: Int => Array(i, i + 10)}
    a.flatMapInt(f).self === a.self.flatMap(x => f(x).toList)
  }

  property("forall") = forAll { a: ofInt =>
    val f = {i: Int => 5 < i }
    a.forall(f) == a.self.forall(f)
  }

  property("map") = forAll { a: ofInt =>
    val f = {i: Int => i - 1 }
    a.mapInt(f).self === a.self.map(f)
  }

  property("reverse.reverse") = forAll { a: ofInt =>
    a.reverse.reverse === a
  }

  property("reverse") = forAll { a: ofInt =>
    a.reverse.self === a.self.reverse
  }

  property("take") = forAll { (a: ofInt, n: Int) =>
    a.take(n).self === a.self.take(n)
  }

  property("takeWhile") = forAll { a: ofInt =>
    val f = {i: Int => 5 < i }
    a.takeWhile(f).self === a.self.takeWhile(f)
  }

  property("takeRight") = forAll { (a: ofInt, n: Int) =>
    a.takeRight(n).self === a.self.toList.takeRight(n).toArray
  }

  property("count") = forAll { a: ofInt =>
    a.count(f) == a.self.count(f)
  }

  property("drop") = forAll { (a: ofInt, n: Int) =>
    a.drop(n).self === a.self.drop(n)
  }

  property("dropWhile") = forAll { a: ofInt =>
    val f = {i: Int => i > 3}
    a.dropWhile(f).self === a.self.dropWhile(f)
  }

  property("dropRight") = forAll { (a: ofInt, n: Int) =>
    a.dropRight(n).self === a.self.toList.dropRight(n).toArray
  }

  property("contains") = forAll { (a: ofInt, n: Int) =>
    a.contains(n) == a.self.contains(n)
  }

  property("splitAt") = forAll { (a: ofInt, n: Int) =>
    val (x1, x2) = a.splitAt(n)
    val (y1, y2) = a.self.splitAt(n)
    (x1.self === y1) && (x2.self === y2)
  }

  property("span") = forAll { a: ofInt =>
    val f = {i: Int => i > 0}
    val (x1, x2) = a.span(f)
    val (y1, y2) = a.self.span(f)
    (x1.self === y1) && (x2.self === y2)
  }

  property("++") = forAll { (a: ofInt, b: ofInt) =>
    (a ++ b).self === (a.self ++ b.self)
  }
}
