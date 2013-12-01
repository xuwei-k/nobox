package nobox

import org.scalacheck._
import Prop.forAll

object TestRef extends TestBase("ofRef"){

  val pf: PartialFunction[Integer, Long] = {case i: Integer if i % 3 == 0 => i + 1}
  val f: Integer => Boolean = {i: Integer => 5 < i && i < 10 }

  property("collect") = forAll { a: ofRef[Integer] =>
    a.collectLong(pf).self must_=== a.self.collect(pf)
  }

  property("collectFirst") = forAll { a: ofRef[Integer] =>
    a.collectFirstLong(pf) must_== a.self.collectFirst(pf)
  }

  property("exists") = forAll { a: ofRef[Integer] =>
    a.exists(f) must_== a.self.exists(f)
  }

  property("filter") = forAll { a: ofRef[Integer] =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  property("find") = forAll { a: ofRef[Integer] =>
    a.find(f) must_== a.self.find(f)
  }

  property("flatMap") = forAll { a: ofRef[Integer] =>
    val f: Integer => Array[Integer] = {i: Integer => Array[Integer](i, i + 10)}
    a.flatMap(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  property("forall") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => 5 < i }
    a.forall(f) must_== a.self.forall(f)
  }

  property("mapInt") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i - 1 }
    a.mapInt(f).self must_=== a.self.map(f)
  }

  property("map") = forAll { a: ofRef[Integer] =>
    val f = (_:Integer).toString
    a.map(f) must_=== a.self.map(f)
  }

  property("reverseMapInt") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i * 2 }
    a.reverseMapInt(f).self must_=== a.self.reverseMap(f)
  }

  property("reverseMap") = forAll { a: ofRef[Integer] =>
    val f1 = (_:Integer).toString
    val f2 = (_:Integer).toByte
    a.reverseMap(f1).self must_=== a.self.reverseMap(f1)
    a.reverseMap(f2).self must_=== a.self.reverseMap(f2)
  }

  property("reverse.reverse") = forAll { a: ofRef[Integer] =>
    a.reverse.reverse === a
  }

  property("reverse") = forAll { a: ofRef[Integer] =>
    a.reverse.self must_=== a.self.reverse
  }

  property("take") = forAll { (a: ofRef[Integer], n: Integer) =>
    a.take(n).self must_=== a.self.take(n)
  }

  property("takeWhile") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => 5 < i }
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  property("takeRight") = forAll { (a: ofRef[Integer], n: Integer) =>
    a.takeRight(n).self must_=== a.self.toList.takeRight(n).toArray
  }

  property("count") = forAll { a: ofRef[Integer] =>
    a.count(f) must_== a.self.count(f)
  }

  property("drop") = forAll { (a: ofRef[Integer], n: Integer) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  property("dropWhile") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i > 3}
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  property("dropRight") = forAll { (a: ofRef[Integer], n: Integer) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  property("contains") = forAll { (a: ofRef[Integer], n: Integer) =>
    a.contains(n) must_== a.self.contains(n)
  }

  property("splitAt") = forAll { (a: ofRef[Integer], n: Integer) =>
    val (x1, x2) = a.splitAt(n)
    val (y1, y2) = a.self.splitAt(n)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("span") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i > 0}
    val (x1, x2) = a.span(f)
    val (y1, y2) = a.self.span(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("++") = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  property("partition") = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i > 0}
    val (x1, x2) = a.partition(f)
    val (y1, y2) = a.self.partition(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  property("reverse_:::") = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    (a reverse_::: b).self must_=== (a.self.toList reverse_::: b.self.toList).toArray
  }

  property("updated") = forAll { (a: ofRef[Integer], index: Int, elem: Integer) =>
    if(0 <= index && index < a.size)
      a.updated(index, elem).self must_=== a.self.updated(index, elem)
    else
      a.updated(index, elem).mustThrowA[IndexOutOfBoundsException]
  }

  property("sum") = forAll { a: ofRef[BigInt] =>
    a.sum must_== a.self.sum
  }

  // TODO product

  property("sorted") = forAll { a: ofRef[Integer] =>
    val o = implicitly[Ordering[Integer]]
    a.sorted.self must_=== a.self.sorted
    a.sorted(o.reverse).self must_=== a.self.sorted(o.reverse)
  }

  property("slice") = forAll { (a: ofRef[Integer], from: Int, until: Int) =>
    a.slice(from, until).self must_=== a.self.slice(from, until)
  }

  property("reduceLeftOption") = forAll { a: ofRef[Integer] =>
    a.reduceLeftOption(_ - _) must_== a.self.reduceLeftOption(_ - _)
  }

  property("reduceRightOption") = forAll { a: ofRef[Integer] =>
    a.reduceRightOption(_ - _) must_== a.self.reduceRightOption(_ - _)
  }

  property("foldLeftInt") = forAll { (a: ofRef[Integer], z: Int) =>
    a.foldLeftInt(z)(_ - _) must_== a.self.foldLeft(z)(_ - _)
  }

  property("foldLeftRef") = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.foldLeftRef(z.toVector)(_ :+ _) must_== a.self.foldLeft(z.toVector)(_ :+ _)
  }

  property("foldRightLong") = forAll { (a: ofRef[Integer], z: Long) =>
    a.foldRightLong(z)(_ - _) must_== a.self.foldRight(z)(_ - _)
  }

  property("foldRightRef") = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.foldRightRef(z)(_ :: _) must_== a.self.foldRight(z)(_ :: _)
  }

  property("indexOf") = forAll { (a: ofRef[Integer], z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  property("lastIndexOf") = forAll { (a: ofRef[Integer], z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  property("mkString") = forAll { (a: ofRef[Integer], start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  property("tails") = forAll { (a: ofRef[Integer]) =>
    a.tails.map(_.self.toSeq).toList must_== a.self.tails.map(_.toSeq).toList
  }

  property("inits") = forAll { (a: ofRef[Integer]) =>
    a.inits.map(_.self.toSeq).toList must_== a.self.inits.map(_.toSeq).toList
  }

  property("tailOption") = forAll { a: ofRef[Integer] =>
    a.tailOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.tail.toSeq)
    )
  }

  property("initOption") = forAll { a: ofRef[Integer] =>
    a.initOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.init.toSeq)
    )
  }

  property("grouped") = forAll { (a: ofRef[Integer], n: Int) =>
    if(n > 0){
      a.grouped(n).map(_.self.toSeq).toList must_== a.self.grouped(n).map(_.toSeq).toList
    }else{
      a.grouped(n).mustThrowA[IllegalArgumentException]
    }
  }

  property("sliding") = forAll { (a: ofRef[Integer], size: Int, step: Int) =>
    if(size > 0 && step > 0){
      a.sliding(size, step).map(_.self.toSeq).toList must_== (
        a.self.sliding(size, step).map(_.toSeq).toList
      )
    }else{
      a.sliding(size, step).mustThrowA[IllegalArgumentException]
    }
  }

  property("max") = forAll { a: ofRef[BigInt] =>
    if(a.self.isEmpty){
      a.max must_== None
    }else{
      a.max must_== Option(a.self.max)
    }
  }

  property("min") = forAll { a: ofRef[BigInt] =>
    if(a.self.isEmpty){
      a.min must_== None
    }else{
      a.min must_== Option(a.self.min)
    }
  }

  property("minmax") = forAll { a: ofRef[BigInt] =>
    if(a.self.isEmpty){
      a.minmax must_== None
    }else{
      a.minmax must_== Option((a.self.min, a.self.max))
    }
  }

  property("scanLeftInt") = forAll { (a: ofRef[Integer], z: Int) =>
    a.scanLeftInt(z)(_ - _).self.toList must_== a.self.scanLeft(z)(_ - _).toList
  }

  property("scanRightInt") = forAll { (a: ofRef[Integer], z: Int) =>
    a.scanRightInt(z)(_ - _).self.toList must_== a.self.scanRight(z)(_ - _).toList
  }

  property("scanLeft") = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.scanLeft(z)(_ :+ _).self.toList must_== a.self.scanLeft(z)(_ :+ _).toList
  }

  property("scanRight") = forAll { (a: ofRef[Integer], z: List[Int]) =>
    a.scanRight(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
  }

  property("scanLeft1") = forAll { a: ofRef[Integer] =>
    a.scanLeft1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.tail.scanLeft(a.self.head)(_ - _).toList
    )
  }

  property("scanRight1") = forAll { a: ofRef[Integer] =>
    a.scanRight1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.init.scanRight(a.self.last)(_ - _).toList
    )
  }

  property("startsWith") = forAll { (a: ofRef[Integer], b: ofRef[Integer], n: Int) =>
    if(n >= 0){
      a.startsWith(b.self, n) must_== a.self.startsWith(b.self, n)
    }else{
      a.startsWith(b.self, n).mustThrowA[IllegalArgumentException]
    }
  }

  property("endsWith") = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    a.endsWith(b.self) must_== a.self.endsWith(b.self)
  }

  property("iterate") = forAll { size: UInt8 =>
    ofRef.iterate("0", size)(a => (a.toInt + 1).toString).self must_=== Array.iterate("0", size)(a => (a.toInt + 1).toString)
  }

  property("tabulate") = forAll { size: UInt8 =>
    ofRef.tabulate(size)(_.toString).self must_=== Array.tabulate(size)(_.toString)
  }

  property("flatten") = forAll { xs: Array[Array[Integer]] =>
    xs.flatten must_=== ofRef.flatten(xs).self
    Array.concat(xs: _*) must_=== ofRef.flatten(xs).self
  }

  property("flatten") = forAll { xs: ofRef[Array[String]] =>
    xs.flatten must_=== xs.self.flatten
  }

  property("flatten") = forAll { xs: ofRef[Array[Int]] =>
    xs.flatten must_=== xs.self.flatten
  }

  property("groupBy") = forAll { (a: ofRef[Integer], x: PInt8) =>
    a.self.groupBy(_ % x).map{case (k, v) => k -> v.toList} must_== (
      a.groupBy(_ % x).map{case (k, v) => k -> v.self.toList}
    )
  }

  property("for comprehension") = forAll { (xs: ofRef[Integer], ys: ofByte) =>
    val a = for{ x <- xs.self; if x % 2 == 0; y <- ys.self } yield (x, y)
    val b = for{ x <- xs; if x % 2 == 0; y <- ys } yield (x, y)

    a must_=== b

    val buf1, buf2 = List.newBuilder[(Int, Byte)]

    for{
      x <- xs.self; if x % 2 == 0; y <- ys.self
    }{ buf1 += ((x, y)) }

    for{
      x <- xs; if x % 2 == 0; y <- ys
    }{ buf2 += ((x, y)) }

    buf1.result must_== buf2.result
  }

  property("toString") = forAll { xs: ofRef[Integer] =>
    xs.toString must_== xs.mkString("ofRef(", ", ", ")")
  }

  property("mkString") = forAll { (xs: ofRef[Integer], start: String, sep: String, end: String) =>
    xs.mkString(start, sep, end) must_== xs.mkString(start, sep, end)
  }

}
