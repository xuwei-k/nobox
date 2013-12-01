package nobox

import org.scalacheck._
import Prop.forAll

object TestInt extends TestBase("ofInt"){

  val pf: PartialFunction[Int, Long] = {case i: Int if i % 3 == 0 => i + 1}
  val f: Int => Boolean = {i: Int => 5 < i && i < 10 }

  property("withFilter.map") = forAll { a: ofInt =>
    a.withFilter(pf isDefinedAt _).map(pf) must_=== a.self.collect(pf)

    val pf2: PartialFunction[Int, String] = {case i: Int if i > 0 => i.toString }
    a.withFilter(pf2 isDefinedAt _).map(pf2) must_=== a.self.collect(pf2)
  }

  property("withFilter.flatMap") = forAll { a: ofInt =>
    val f1 = (_: Int) % 3 != 0
    val f2 = (a: Int) => Array(a, a + 1)
    a.withFilter(f1).flatMap(f2) must_=== a.self.filter(f1).flatMap(f2 andThen (_.toSeq))

    val f3 = (a: Int) => Array(a.toString)
    a.withFilter(f1).flatMap(f3) must_=== a.self.filter(f1).flatMap(f3 andThen (_.toSeq))
  }

  property("withFilter.withFilter") = forAll { a: ofInt =>
    val f1 = (_: Int) % 2 != 0
    val f2 = (_: Int) % 3 != 0
    a.withFilter(f1).withFilter(f2).map(x => x) must_=== a.self.withFilter(f1).withFilter(f2).map(x => x)
  }

  property("withFilter.foreach") = forAll { a: ofInt =>
    val b1, b2 = collection.mutable.ArrayBuilder.make[String]
    a.withFilter(f).foreach(b1 += _.toString)
    a.self.withFilter(f).foreach(b2 += _.toString)
    b1.result must_=== b2.result
  }

  property("collectLong") = forAll { a: ofInt =>
    a.collectLong(pf).self must_=== a.self.collect(pf)
  }

  property("collectRef") = forAll { a: ofInt =>
    val pf: PartialFunction[Int, String] = {case i if i > 0 => i.toString}
    a.collectRef(pf).self must_=== a.self.collect(pf)
  }

  property("collectFirstLong") = forAll { a: ofInt =>
    a.collectFirstLong(pf) must_== a.self.collectFirst(pf)
  }

  property("collectFirstRef") = forAll { a: ofInt =>
    val pf: PartialFunction[Int, String] = {case i if i > 0 => i.toString}
    a.collectFirstRef(pf) must_== a.self.collectFirst(pf)
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

  property("flatMapInt") = forAll { a: ofInt =>
    val f: Int => Array[Int] = {i: Int => Array(i, i + 10)}
    a.flatMapInt(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  property("flatMapRef") = forAll { a: ofInt =>
    val f: Int => Array[String] = {i: Int => Array(i.toString)}
    a.flatMapRef(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  property("forall") = forAll { a: ofInt =>
    val f = {i: Int => 5 < i }
    a.forall(f) must_== a.self.forall(f)
  }

  property("mapInt") = forAll { a: ofInt =>
    val f = {i: Int => i - 1 }
    a.mapInt(f).self must_=== a.self.map(f)
  }

  property("mapRef") = forAll { a: ofInt =>
    val f = (_:Int).toString
    a.mapRef(f).self must_=== a.self.map(f)
  }

  property("map") = forAll { a: ofInt =>
    val f = (_:Int).toString
    a.map(f) must_=== a.self.map(f)
  }

  property("reverseMapInt") = forAll { a: ofInt =>
    val f = {i: Int => i * 2 }
    a.reverseMapInt(f).self must_=== a.self.reverseMap(f)
  }

  property("reverseRef") = forAll { a: ofInt =>
    val f = (_:Int).toString
    a.reverseMapRef(f).self must_=== a.self.reverseMap(f)
  }

  property("reverseMap") = forAll { a: ofInt =>
    val f1 = (_:Int).toString
    val f2 = (_:Int).toByte
    a.reverseMap(f1).self must_=== a.self.reverseMap(f1)
    a.reverseMap(f2).self must_=== a.self.reverseMap(f2)
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

  property("foldMapLeft1Long") = forAll { a: ofInt =>
    val z = (_: Int).toLong + 10
    a.foldMapLeft1Long(z)(_ - _) must_== {
      if(a.length == 0) None
      else Some(a.self.tail.foldLeft(z(a.self.head))(_ - _))
    }
  }

  property("foldMapLeft1") = forAll { a: ofInt =>
    val z = (_: Int).toString
    a.foldMapLeft1(z)(_ + _) must_== {
      if(a.length == 0) None
      else Some(a.self.tail.foldLeft(z(a.self.head))(_ + _))
    }
  }

  property("foldMapRight1Int") = forAll { a: ofInt =>
    val z = (_: Int) + 10
    a.foldMapRight1Int(z)(_ - _) must_== {
      if(a.length == 0) None
      else Some(a.self.init.foldRight(z(a.self.last))(_ - _))
    }
  }

  property("foldMapRight1") = forAll { a: ofInt =>
    val z = (_: Int) + 10
    a.foldMapRight1(z)(_ - _) must_== {
      if(a.length == 0) None
      else Some(a.self.init.foldRight(z(a.self.last))(_ - _))
    }
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

  property("foldLeftRef") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldLeftRef(z.toVector)(_ :+ _) must_== a.self.foldLeft(z.toVector)(_ :+ _)
  }

  property("foldRightLong") = forAll { (a: ofInt, z: Long) =>
    a.foldRightLong(z)(_ - _) must_== a.self.foldRight(z)(_ - _)
  }

  property("foldRightRef") = forAll { (a: ofInt, z: List[Int]) =>
    a.foldRightRef(z)(_ :: _) must_== a.self.foldRight(z)(_ :: _)
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

  property("maxBy") = forAll { a: ofInt =>
    val f = (_: Int).toHexString
    a.maxBy(f) must_== (
      if(a.size == 0) None
      else Some(a.self.maxBy(f))
    )
  }

  property("minBy") = forAll { a: ofInt =>
    val f = (_: Int).toHexString
    a.minBy(f) must_== (
      if(a.size == 0) None
      else Some(a.self.minBy(f))
    )
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

  property("minmax") = forAll { a: ofInt =>
    if(a.self.isEmpty){
      a.min must_== None
    }else{
      a.minmax must_== Some((a.self.min, a.self.max))
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

  property("scanRightRef") = forAll { (a: ofInt, z: List[Int]) =>
    a.scanRightRef(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
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

  property("iterate") = forAll { (start: Int, size: UInt8) =>
    ofInt.iterate(start, size)(_ + 1).self must_=== Array.iterate(start, size)(_ + 1)
  }

  property("tabulate") = forAll { size: UInt8 =>
    ofInt.tabulate(size)(_ + 1).self must_=== Array.tabulate(size)(_ + 1)
  }

  property("flatten") = forAll { xs: Array[Array[Int]] =>
    xs.flatten must_=== ofInt.flatten(xs).self
    Array.concat(xs: _*) must_=== ofInt.flatten(xs).self
  }

  property("groupBy") = forAll { (a: ofInt, x: PInt8) =>
    a.self.groupBy(_ % x).map{case (k, v) => k -> v.toList} must_== (
      a.groupBy(_ % x).map{case (k, v) => k -> v.self.toList}
    )
  }

  property("for comprehension") = forAll { (xs: ofInt, ys: ofByte) =>
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

  property("toString") = forAll { xs: ofInt =>
    xs.toString must_== xs.mkString("ofInt(", ", ", ")")
  }

  property("mkString") = forAll { (xs: ofInt, start: String, sep: String, end: String) =>
    xs.mkString(start, sep, end) must_== xs.mkString(start, sep, end)
  }

  property("unfold") = {
    ofInt.unfold(0)(a => if(a < 10) Some((a + 1, a + 1)) else None) === ofInt.iterate(1, 10)(_ + 1)
  }

  property("deleteFirst") = forAll { (xs: ofInt, e: Int) =>
    if(xs.contains(e)){
      val a = xs.deleteFirst(e)
      a.size must_== (xs.size - 1)
      val i = xs.indexOf(e).get
      a.self must_=== (xs.take(i) ++ xs.drop(i + 1)).self
    }else{
      xs.deleteFirst(e) must_== xs
    }
  }
}
