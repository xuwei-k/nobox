package nobox

import org.scalacheck._
import Prop.forAll

object Test extends Properties("nobox"){
  def fail(message: String) =
    throw new AssertionError(message)

  implicit class AnyOps(actual: => Any) {
    def mustThrowA[T <: Throwable](implicit man: reflect.ClassTag[T]): Boolean = {
      val erasedClass = man.runtimeClass
      try {
        actual
        fail("no exception thrown, expected " + erasedClass)
      } catch {
        case ex: Throwable =>
          if (!erasedClass.isInstance(ex))
            fail("wrong exception thrown, expected: " + erasedClass + " got: " + ex)
          else
            true
      }
    }
  }

  implicit class ArrayOps[A](val self: Array[A]) extends AnyVal {
    def ===(that: Array[A]): Boolean = {
      if(self sameElements that) true
      else {
        val msg = self.mkString("Array(",",",")") + " is not equal " + that.mkString("Array(",",",")")
        fail(msg)
      }
    }
  }

  implicit val ofIntArb: Arbitrary[ofInt] =
    Arbitrary(implicitly[Arbitrary[Array[Int]]].arbitrary.map(array => ofInt(array: _*)))

  implicit val ofFloatArb: Arbitrary[ofFloat] =
    Arbitrary(implicitly[Arbitrary[Array[Float]]].arbitrary.map(array => ofFloat(array: _*)))

  val pf: PartialFunction[Int, Long] = {case i: Int if i % 3 == 0 => i + 1}
  val f: Int => Boolean = {i: Int => 5 < i && i < 10 }

  property("collect") = forAll { a: ofInt =>
    a.collectLong(pf).self === a.self.collect(pf)
  }

  property("collectFirst") = forAll { a: ofInt =>
    a.collectFirstLong(pf) == a.self.collectFirst(pf)
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

  property("partition") = forAll { a: ofInt =>
    val f = {i: Int => i > 0}
    val (x1, x2) = a.partition(f)
    val (y1, y2) = a.self.partition(f)
    (x1.self === y1) && (x2.self === y2)
  }

  property("reverse_:::") = forAll { (a: ofInt, b: ofInt) =>
    (a reverse_::: b).self === (a.self.toList reverse_::: b.self.toList).toArray
  }

  property("updated") = forAll { (a: ofInt, index: Int, elem: Int) =>
    if(0 <= index && index < a.size)
      a.updated(index, elem).self === a.self.updated(index, elem)
    else
      a.updated(index, elem).mustThrowA[IndexOutOfBoundsException]
  }

  property("sum ofInt") = forAll { a: ofInt =>
    a.sum == a.self.sum
  }

  property("sum ofFloat") = forAll { a: ofFloat =>
    a.sum == a.self.sum
  }

  property("sorted") = forAll { a: ofFloat =>
    a.sorted.self === a.self.sorted
  }

  property("slice") = forAll { (a: ofInt, from: Int, until: Int) =>
    a.slice(from, until).self === a.self.slice(from, until)
  }

  property("reduceLeftOption") = forAll { a: ofInt =>
    a.reduceLeftOption(_ - _) == a.self.reduceLeftOption(_ - _)
  }

  property("reduceRightOption") = forAll { a: ofInt =>
    a.reduceRightOption(_ - _) == a.self.reduceRightOption(_ - _)
  }

  property("foldLeftInt") = forAll { (a: ofInt, z: Int) =>
    a.foldLeftInt(z)(_ - _) == a.self.foldLeft(z)(_ - _)
  }

  property("foldLeft") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldLeft(z.toVector)(_ :+ _) == a.self.foldLeft(z.toVector)(_ :+ _)
  }

  property("foldRightLong") = forAll { (a: ofInt, z: Long) =>
    a.foldRightLong(z)(_ - _) == a.self.foldRight(z)(_ - _)
  }

  property("foldRight") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldRight(z)(_ :: _) == a.self.foldRight(z)(_ :: _)
  }

}
