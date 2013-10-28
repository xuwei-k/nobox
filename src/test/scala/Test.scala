package nobox

import org.scalacheck._
import Prop.forAll

object Test extends Properties("nobox"){
  def fail(message: String) =
    throw new AssertionError(message)

  implicit class AnyOps[A](actual: => A) {
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

    def must_==(that: A): Boolean = {
      val self = actual
      if(self == that) true
      else {
        fail(self + " is not equal to " + that)
      }
    }
  }

  implicit class ArrayOps[A](val self: Array[A]) extends AnyVal {
    def must_===(that: Array[A]): Boolean = {
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
    a.collectLong(pf).self must_=== a.self.collect(pf)
  }

  property("collectFirst") = forAll { a: ofInt =>
    a.collectFirstLong(pf) must_== a.self.collectFirst(pf)
  }

  property("exists") = forAll { a: ofInt =>
    a.exists(f) must_== a.self.exists(f)
  }

  property("filter") = forAll { a: ofInt =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  property("find") = forAll { a: ofInt =>
    a.find(f) must_== a.self.find(f)
  }

  property("flatMap") = forAll { a: ofInt =>
    val f: Int => Array[Int] = {i: Int => Array(i, i + 10)}
    a.flatMapInt(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  property("forall") = forAll { a: ofInt =>
    val f = {i: Int => 5 < i }
    a.forall(f) must_== a.self.forall(f)
  }

  property("map") = forAll { a: ofInt =>
    val f = {i: Int => i - 1 }
    a.mapInt(f).self must_=== a.self.map(f)
  }

  property("reverseMap") = forAll { a: ofInt =>
    val f = {i: Int => i * 2 }
    a.reverseMapInt(f).self must_=== a.self.reverseMap(f)
  }

  property("reverse.reverse") = forAll { a: ofInt =>
    a.reverse.reverse === a
  }

  property("reverse") = forAll { a: ofInt =>
    a.reverse.self must_=== a.self.reverse
  }

  property("take") = forAll { (a: ofInt, n: Int) =>
    a.take(n).self must_=== a.self.take(n)
  }

  property("takeWhile") = forAll { a: ofInt =>
    val f = {i: Int => 5 < i }
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  property("takeRight") = forAll { (a: ofInt, n: Int) =>
    a.takeRight(n).self must_=== a.self.toList.takeRight(n).toArray
  }

  property("count") = forAll { a: ofInt =>
    a.count(f) must_== a.self.count(f)
  }

  property("drop") = forAll { (a: ofInt, n: Int) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  property("dropWhile") = forAll { a: ofInt =>
    val f = {i: Int => i > 3}
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  property("dropRight") = forAll { (a: ofInt, n: Int) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  property("contains") = forAll { (a: ofInt, n: Int) =>
    a.contains(n) must_== a.self.contains(n)
  }

  property("splitAt") = forAll { (a: ofInt, n: Int) =>
    val (x1, x2) = a.splitAt(n)
    val (y1, y2) = a.self.splitAt(n)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("span") = forAll { a: ofInt =>
    val f = {i: Int => i > 0}
    val (x1, x2) = a.span(f)
    val (y1, y2) = a.self.span(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("++") = forAll { (a: ofInt, b: ofInt) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  property("partition") = forAll { a: ofInt =>
    val f = {i: Int => i > 0}
    val (x1, x2) = a.partition(f)
    val (y1, y2) = a.self.partition(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("reverse_:::") = forAll { (a: ofInt, b: ofInt) =>
    (a reverse_::: b).self must_=== (a.self.toList reverse_::: b.self.toList).toArray
  }

  property("updated") = forAll { (a: ofInt, index: Int, elem: Int) =>
    if(0 <= index && index < a.size)
      a.updated(index, elem).self must_=== a.self.updated(index, elem)
    else
      a.updated(index, elem).mustThrowA[IndexOutOfBoundsException]
  }

  property("sum ofInt") = forAll { a: ofInt =>
    a.sum must_== a.self.sum
  }

  property("sum ofFloat") = forAll { a: ofFloat =>
    a.sum must_== a.self.sum
  }

  property("sumLong") = forAll { a: ofInt =>
    a.sumLong must_== a.self.map(_.toLong).sum
  }

  property("product ofInt") = forAll { a: ofInt =>
    a.product must_== a.self.product
  }

  property("product ofFloat") = forAll { a: ofFloat =>
    val x = a.product
    val y = a.self.product
    (x == y) || (x.isNaN && y.isNaN)
  }

  property("productLong") = forAll { a: ofInt =>
    a.productLong must_== a.self.map(_.toLong).product
  }

  property("productDouble") = forAll { a: ofInt =>
    val x = a.productDouble
    val y = a.self.map(_.toDouble).product
    (x == y) || (x.isNaN && y.isNaN)
  }

  property("sorted") = forAll { a: ofFloat =>
    a.sorted.self must_=== a.self.sorted
  }

  property("slice") = forAll { (a: ofInt, from: Int, until: Int) =>
    a.slice(from, until).self must_=== a.self.slice(from, until)
  }

  property("reduceLeftOption") = forAll { a: ofInt =>
    a.reduceLeftOption(_ - _) must_== a.self.reduceLeftOption(_ - _)
  }

  property("reduceRightOption") = forAll { a: ofInt =>
    a.reduceRightOption(_ - _) must_== a.self.reduceRightOption(_ - _)
  }

  property("foldLeftInt") = forAll { (a: ofInt, z: Int) =>
    a.foldLeftInt(z)(_ - _) must_== a.self.foldLeft(z)(_ - _)
  }

  property("foldLeft") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldLeft(z.toVector)(_ :+ _) must_== a.self.foldLeft(z.toVector)(_ :+ _)
  }

  property("foldRightLong") = forAll { (a: ofInt, z: Long) =>
    a.foldRightLong(z)(_ - _) must_== a.self.foldRight(z)(_ - _)
  }

  property("foldRight") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldRight(z)(_ :: _) must_== a.self.foldRight(z)(_ :: _)
  }

  property("indexOf") = forAll { (a: ofInt, z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  property("lastIndexOf") = forAll { (a: ofInt, z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  property("mkString") = forAll { (a: ofInt, start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  property("tails") = forAll { (a: ofInt) =>
    a.tails.map(_.self.toSeq).toList must_== a.self.tails.map(_.toSeq).toList
  }

  property("inits") = forAll { (a: ofInt) =>
    a.inits.map(_.self.toSeq).toList must_== a.self.inits.map(_.toSeq).toList
  }

  property("tailOption") = forAll { a: ofInt =>
    a.tailOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.tail.toSeq)
    )
  }

  property("initOption") = forAll { a: ofInt =>
    a.initOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.init.toSeq)
    )
  }

  property("grouped") = forAll { (a: ofInt, n: Int) =>
    if(n > 0){
      a.grouped(n).map(_.self.toSeq).toList must_== a.self.grouped(n).map(_.toSeq).toList
    }else{
      a.grouped(n).mustThrowA[IllegalArgumentException]
    }
  }

  property("sliding") = forAll { (a: ofInt, size: Int, step: Int) =>
    if(size > 0 && step > 0){
      a.sliding(size, step).map(_.self.toSeq).toList must_== (
        a.self.sliding(size, step).map(_.toSeq).toList
      )
    }else{
      a.sliding(size, step).mustThrowA[IllegalArgumentException]
    }
  }

  property("max") = forAll { a: ofInt =>
    if(a.self.isEmpty){
      a.max must_== None
    }else{
      a.max must_== Some(a.self.max)
    }
  }

  property("min") = forAll { a: ofInt =>
    if(a.self.isEmpty){
      a.min must_== None
    }else{
      a.min must_== Some(a.self.min)
    }
  }

  property("scanLeftInt") = forAll { (a: ofInt, z: Int) =>
    a.scanLeftInt(z)(_ - _).self.toList must_== a.self.scanLeft(z)(_ - _).toList
  }

  property("scanRightInt") = forAll { (a: ofInt, z: Int) =>
    a.scanRightInt(z)(_ - _).self.toList must_== a.self.scanRight(z)(_ - _).toList
  }

  property("scanLeft") = forAll { (a: ofInt, z: List[Int]) =>
    a.scanLeft(z)(_ :+ _).self.toList must_== a.self.scanLeft(z)(_ :+ _).toList
  }

  property("scanRight") = forAll { (a: ofInt, z: List[Int]) =>
    a.scanRight(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
  }

  property("scanLeft1") = forAll { a: ofInt =>
    a.scanLeft1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.tail.scanLeft(a.self.head)(_ - _).toList
    )
  }

  property("scanRight1") = forAll { a: ofInt =>
    a.scanRight1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.init.scanRight(a.self.last)(_ - _).toList
    )
  }

  property("startsWith") = forAll { (a: ofInt, b: ofInt, n: Int) =>
    if(n >= 0){
      a.startsWith(b.self, n) must_== a.self.startsWith(b.self, n)
    }else{
      a.startsWith(b.self, n).mustThrowA[IllegalArgumentException]
    }
  }

  property("endsWith") = forAll { (a: ofInt, b: ofInt) =>
    a.endsWith(b.self) must_== a.self.endsWith(b.self)
  }

  property("iterate") = forAll { (start: Int, size: Byte) =>
    ofInt.iterate(start, size.toInt.abs)(_ + 1).self must_=== Array.iterate(start, size.toInt.abs)(_ + 1)
  }

  property("tabulate") = forAll { size: Byte =>
    ofInt.tabulate(size.toInt.abs)(_ + 1).self must_=== Array.tabulate(size.toInt.abs)(_ + 1)
  }

  property("flatten") = forAll { xs: Array[Array[Int]] =>
    xs.flatten must_=== ofInt.flatten(xs).self
    Array.concat(xs: _*) must_=== ofInt.flatten(xs).self
  }

  property("groupBy") = forAll { (a: ofInt, n: Byte) =>
    val x = n.toInt.abs + 1
    a.self.groupBy(_ % x).map{case (k, v) => k -> v.toList} must_== (
      a.groupBy(_ % x).map{case (k, v) => k -> v.self.toList}
    )
  }
}
