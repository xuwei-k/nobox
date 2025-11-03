package nobox

import scalaprops.Property.forAll

object TestInt extends TestBase {

  val `withFilter.map` = forAll { (a: ofInt, pf: PartialFunction[Int, Long]) =>
    a.withFilter(pf isDefinedAt _).map(pf) must_=== a.self.collect(pf)

    val pf2: PartialFunction[Int, String] = { case i: Int if i > 0 => i.toString }
    a.withFilter(pf2 isDefinedAt _).map(pf2) must_=== a.self.collect(pf2)
  }

  val `withFilter.flatMap` = forAll { (a: ofInt) =>
    val f1 = (_: Int) % 3 != 0
    val f2 = (a: Int) => Array(a, a + 1)
    a.withFilter(f1).flatMap(f2) must_=== a.self.filter(f1).flatMap(f2 andThen (_.toSeq))

    val f3 = (a: Int) => Array(a.toString)
    a.withFilter(f1).flatMap(f3) must_=== a.self.filter(f1).flatMap(f3 andThen (_.toSeq))
  }

  val `withFilter.withFilter` = forAll { (a: ofInt) =>
    val f1 = (_: Int) % 2 != 0
    val f2 = (_: Int) % 3 != 0
    a.withFilter(f1).withFilter(f2).map(x => x) must_=== a.self.withFilter(f1).withFilter(f2).map(x => x)
  }

  val `withFilter.foreach` = forAll { (a: ofInt, f: Int => Boolean) =>
    val b1, b2 = collection.mutable.ArrayBuilder.make[String]
    a.withFilter(f).foreach(b1 += _.toString)
    a.self.withFilter(f).foreach(b2 += _.toString)
    b1.result() must_=== b2.result()
  }

  val collectLong = forAll { (a: ofInt, pf: PartialFunction[Int, Long]) =>
    a.collectLong(pf).self must_=== a.self.collect(pf)
  }

  val collectRef = forAll { (a: ofInt) =>
    val pf: PartialFunction[Int, String] = { case i if i > 0 => i.toString }
    a.collectRef(pf).self must_=== a.self.collect(pf)
  }

  val collectFirstLong = forAll { (a: ofInt, pf: PartialFunction[Int, Long]) =>
    a.collectFirstLong(pf) must_== a.self.collectFirst(pf)
  }

  val collectFirstRef = forAll { (a: ofInt) =>
    val pf: PartialFunction[Int, String] = { case i if i > 0 => i.toString }
    a.collectFirstRef(pf) must_== a.self.collectFirst(pf)
  }

  val exists = forAll { (a: ofInt, f: Int => Boolean) =>
    a.exists(f) must_== a.self.exists(f)
  }

  val filter = forAll { (a: ofInt, f: Int => Boolean) =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  val find = forAll { (a: ofInt, f: Int => Boolean) =>
    a.find(f) must_== a.self.find(f)
  }

  val flatMapInt = forAll { (a: ofInt) =>
    val f: Int => Array[Int] = { (i: Int) => Array(i, i + 10) }
    a.flatMapInt(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  val flatMapRef = forAll { (a: ofInt) =>
    val f: Int => Array[String] = { (i: Int) => Array(i.toString) }
    a.flatMapRef(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  val forall = forAll { (a: ofInt) =>
    val f = { (i: Int) => 5 < i }
    a.forall(f) must_== a.self.forall(f)
  }

  val mapInt = forAll { (a: ofInt) =>
    val f = { (i: Int) => i - 1 }
    a.mapInt(f).self must_=== a.self.map(f)
  }

  val mapRef = forAll { (a: ofInt) =>
    val f = (_: Int).toString
    a.mapRef(f).self must_=== a.self.map(f)
  }

  val map = forAll { (a: ofInt) =>
    val f = (_: Int).toString
    a.map(f) must_=== a.self.map(f)
  }

  val reverseMapInt = forAll { (a: ofInt) =>
    val f = { (i: Int) => i * 2 }
    a.reverseMapInt(f).self must_=== a.self.reverseMap(f).toArray
  }

  val reverseRef = forAll { (a: ofInt) =>
    val f = (_: Int).toString
    a.reverseMapRef(f).self must_=== a.self.reverseMap(f).toArray
  }

  val reverseMap = forAll { (a: ofInt) =>
    val f1 = (_: Int).toString
    val f2 = (_: Int).toByte
    a.reverseMap(f1).self must_=== a.self.reverseMap(f1).toArray
    a.reverseMap(f2).self must_=== a.self.reverseMap(f2).toArray
  }

  val `reverse.reverse` = forAll { (a: ofInt) =>
    a.reverse.reverse === a
  }

  val reverse = forAll { (a: ofInt) =>
    a.reverse.self must_=== a.self.reverse
  }

  val take = forAll { (a: ofInt, n: Int) =>
    a.take(n).self must_=== a.self.take(n)
  }

  val takeWhile = forAll { (a: ofInt) =>
    val f = { (i: Int) => 5 < i }
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  val takeWhileR = forAll { (a: ofInt) =>
    val f = { (i: Int) => 5 < i }
    a.takeWhileR(f).self must_=== a.self.reverse.takeWhile(f).reverse
  }

  val takeRight = forAll { (a: ofInt, n: Int) =>
    a.takeRight(n).self must_=== a.self.toList.takeRight(n).toArray
  }

  val count = forAll { (a: ofInt, f: Int => Boolean) =>
    a.count(f) must_== a.self.count(f)
  }

  val drop = forAll { (a: ofInt, n: Int) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  val dropWhile = forAll { (a: ofInt) =>
    val f = { (i: Int) => i > 3 }
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  val dropWhileR = forAll { (a: ofInt) =>
    val f = { (i: Int) => i > 10 }
    a.dropWhileR(f).self must_=== a.self.reverse.dropWhile(f).reverse
  }

  val dropRight = forAll { (a: ofInt, n: Int) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  val contains = forAll { (a: ofInt, n: Int) =>
    a.contains(n) must_== a.self.contains(n)
  }

  val splitAt = forAll { (a: ofInt, n: Int) =>
    val (x1, x2) = a.splitAt(n)
    val (y1, y2) = a.self.splitAt(n)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val span = forAll { (a: ofInt) =>
    val f = { (i: Int) => i > 0 }
    val (x1, x2) = a.span(f)
    val (y1, y2) = a.self.span(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val ++ = forAll { (a: ofInt, b: ofInt) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  val partition = forAll { (a: ofInt) =>
    val f = { (i: Int) => i > 0 }
    val (x1, x2) = a.partition(f)
    val (y1, y2) = a.self.partition(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val reverse_::: = forAll { (a: ofInt, b: ofInt) =>
    (a reverse_::: b).self must_=== (a.self.toList reverse_::: b.self.toList).toArray
  }

  val updated = forAll { (a: ofInt, index: Int, elem: Int) =>
    if (0 <= index && index < a.size)
      a.updated(index, elem).self must_=== a.self.updated(index, elem)
    else
      a.updated(index, elem).mustThrowA[IndexOutOfBoundsException]
  }

  val `sum ofInt` = forAll { (a: ofInt) =>
    a.sum must_== a.self.sum
  }

  val `sum ofFloat` = forAll { (a: ofFloat) =>
    a.sum must_== a.self.sum
  }

  val sumLong = forAll { (a: ofInt) =>
    a.sumLong must_== a.self.map(_.toLong).sum
  }

  val `product ofInt` = forAll { (a: ofInt) =>
    a.product must_== a.self.product
  }

  val `product ofFloat` = forAll { (a: ofFloat) =>
    val x = a.product
    val y = a.self.product
    (x == y) || (x.isNaN && y.isNaN)
  }

  val productLong = forAll { (a: ofInt) =>
    a.productLong must_== a.self.map(_.toLong).product
  }

  val productDouble = forAll { (a: ofInt) =>
    val x = a.productDouble
    val y = a.self.map(_.toDouble).product
    (x == y) || (x.isNaN && y.isNaN)
  }

  val sorted = forAll { (a: ofFloat) =>
    a.sorted.self must_=== a.self.sorted
  }

  val slice = forAll { (a: ofInt, from: Int, until: Int) =>
    a.slice(from, until).self must_=== a.self.slice(from, until)
  }

  val foldMapLeft1Long = forAll { (a: ofInt) =>
    val z = (_: Int).toLong + 10
    a.foldMapLeft1Long(z)(_ - _) must_== {
      if (a.length == 0) None
      else Some(a.self.tail.foldLeft(z(a.self.head))(_ - _))
    }
  }

  val foldMapLeft1 = forAll { (a: ofInt) =>
    val z = (_: Int).toString
    a.foldMapLeft1(z)(_ + _) must_== {
      if (a.length == 0) None
      else Some(a.self.tail.foldLeft(z(a.self.head))(_ + _))
    }
  }

  val foldMapRight1Int = forAll { (a: ofInt) =>
    val z = (_: Int) + 10
    a.foldMapRight1Int(z)(_ - _) must_== {
      if (a.length == 0) None
      else Some(a.self.init.foldRight(z(a.self.last))(_ - _))
    }
  }

  val foldMapRight1 = forAll { (a: ofInt) =>
    val z = (_: Int) + 10
    a.foldMapRight1(z)(_ - _) must_== {
      if (a.length == 0) None
      else Some(a.self.init.foldRight(z(a.self.last))(_ - _))
    }
  }

  val reduceLeftOption = forAll { (a: ofInt) =>
    a.reduceLeftOption(_ - _) must_== a.self.reduceLeftOption(_ - _)
  }

  val reduceRightOption = forAll { (a: ofInt) =>
    a.reduceRightOption(_ - _) must_== a.self.reduceRightOption(_ - _)
  }

  val foldLeftInt = forAll { (a: ofInt, z: Int) =>
    a.foldLeftInt(z)(_ - _) must_== a.self.foldLeft(z)(_ - _)
  }

  val foldLeftRef = forAll { (a: ofInt, z: List[Int]) =>
    a.foldLeftRef(z.toVector)(_ :+ _) must_== a.self.foldLeft(z.toVector)(_ :+ _)
  }

  val foldRightLong = forAll { (a: ofInt, z: Long) =>
    a.foldRightLong(z)(_ - _) must_== a.self.foldRight(z)(_ - _)
  }

  val foldRightRef = forAll { (a: ofInt, z: List[Int]) =>
    a.foldRightRef(z)(_ :: _) must_== a.self.foldRight(z)(_ :: _)
  }

  val indexOf = forAll { (a: ofInt, z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  val lastIndexOf = forAll { (a: ofInt, z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  val mkString = forAll { (a: ofInt, start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  val tails = forAll { (a: ofInt) =>
    a.tails.map(_.self.toSeq).toList must_== a.self.tails.map(_.toSeq).toList
  }

  val inits = forAll { (a: ofInt) =>
    a.inits.map(_.self.toSeq).toList must_== a.self.inits.map(_.toSeq).toList
  }

  val tailOption = forAll { (a: ofInt) =>
    a.tailOption.map(_.self.toSeq) must_== (
      if (a.self.isEmpty) None
      else Some(a.self.tail.toSeq)
    )
  }

  val initOption = forAll { (a: ofInt) =>
    a.initOption.map(_.self.toSeq) must_== (
      if (a.self.isEmpty) None
      else Some(a.self.init.toSeq)
    )
  }

  val grouped = forAll { (a: ofInt, n: Int) =>
    if (n > 0) {
      a.grouped(n).map(_.self.toSeq).toList must_== a.self.grouped(n).map(_.toSeq).toList
    } else {
      a.grouped(n).mustThrowA[IllegalArgumentException]
    }
  }

  val sliding = forAll { (a: ofInt, size: Int, step: Int) =>
    if (size > 0 && step > 0) {
      a.sliding(size, step).map(_.self.toSeq).toList must_== (
        a.self.sliding(size, step).map(_.toSeq).toList
      )
    } else {
      a.sliding(size, step).mustThrowA[IllegalArgumentException]
    }
  }

  val maxBy = forAll { (a: ofInt) =>
    val f = (_: Int).toHexString
    a.maxBy(f) must_== (
      if (a.size == 0) None
      else Some(a.self.maxBy(f))
    )
  }

  val minBy = forAll { (a: ofInt) =>
    val f = (_: Int).toHexString
    a.minBy(f) must_== (
      if (a.size == 0) None
      else Some(a.self.minBy(f))
    )
  }

  val max = forAll { (a: ofInt) =>
    if (a.self.isEmpty) {
      a.max must_== None
    } else {
      a.max must_== Some(a.self.max)
    }
  }

  val min = forAll { (a: ofInt) =>
    if (a.self.isEmpty) {
      a.min must_== None
    } else {
      a.min must_== Some(a.self.min)
    }
  }

  val minmax = forAll { (a: ofInt) =>
    if (a.self.isEmpty) {
      a.min must_== None
    } else {
      a.minmax must_== Some((a.self.min, a.self.max))
    }
  }

  val scanLeftInt = forAll { (a: ofInt, z: Int) =>
    a.scanLeftInt(z)(_ - _).self.toList must_== a.self.scanLeft(z)(_ - _).toList
  }

  val scanRightInt = forAll { (a: ofInt, z: Int) =>
    a.scanRightInt(z)(_ - _).self.toList must_== a.self.scanRight(z)(_ - _).toList
  }

  val scanLeft = forAll { (a: ofInt, z: List[Int]) =>
    a.scanLeft(z)(_ :+ _).self.toList must_== a.self.scanLeft(z)(_ :+ _).toList
  }

  val scanRight = forAll { (a: ofInt, z: List[Int]) =>
    a.scanRight(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
  }

  val scanRightRef = forAll { (a: ofInt, z: List[Int]) =>
    a.scanRightRef(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
  }

  val scanLeft1 = forAll { (a: ofInt) =>
    a.scanLeft1(_ - _).self.toList must_== (
      if (a.self.isEmpty) List()
      else a.self.tail.scanLeft(a.self.head)(_ - _).toList
    )
  }

  val scanRight1 = forAll { (a: ofInt) =>
    a.scanRight1(_ - _).self.toList must_== (
      if (a.self.isEmpty) List()
      else a.self.init.scanRight(a.self.last)(_ - _).toList
    )
  }

  val startsWith = forAll { (a: ofInt, b: ofInt, n: Int) =>
    if (n >= 0) {
      a.startsWith(b.self, n) must_== a.self.startsWith(b.self, n)
    } else {
      a.startsWith(b.self, n).mustThrowA[IllegalArgumentException]
    }
  }

  val endsWith = forAll { (a: ofInt, b: ofInt) =>
    a.endsWith(b.self) must_== a.self.endsWith(b.self)
  }

  val iterate = forAll { (start: Int, size: UInt8) =>
    ofInt.iterate(start, size)(_ + 1).self must_=== Array.iterate(start, size)(_ + 1)
  }

  val tabulate = forAll { (size: UInt8) =>
    ofInt.tabulate(size)(_ + 1).self must_=== Array.tabulate(size)(_ + 1)
  }

  val flatten = forAll { (xs: Array[Array[Int]]) =>
    xs.flatten must_=== ofInt.flatten(xs).self
    Array.concat(xs*) must_=== ofInt.flatten(xs).self
  }

  val groupBy = forAll { (a: ofInt, x: PInt8) =>
    a.self.groupBy(_ % x).map { case (k, v) => k -> v.toList } must_== (
      a.groupBy(_ % x).map { case (k, v) => k -> v.self.toList }
    )
  }

  val `for comprehension` = forAll { (xs: ofInt, ys: ofByte) =>
    val a = for {
      x <- xs.self if x % 2 == 0
      y <- ys.self
    } yield (x, y)
    val b = for {
      x <- xs if x % 2 == 0
      y <- ys
    } yield (x, y)

    a must_=== b

    val buf1, buf2 = List.newBuilder[(Int, Byte)]

    for {
      x <- xs.self if x % 2 == 0
      y <- ys.self
    } { buf1 += ((x, y)) }

    for {
      x <- xs if x % 2 == 0
      y <- ys
    } { buf2 += ((x, y)) }

    buf1.result() must_== buf2.result()
  }

  val testToString = forAll { (xs: ofInt) =>
    xs.toString must_== xs.mkString("ofInt(", ", ", ")")
  }

  val unfold = forAll {
    ofInt.unfold(0)(a => if (a < 10) Some((a + 1, a + 1)) else None) === ofInt.iterate(1, 10)(_ + 1)
  }

  val deleteFirst = forAll { (xs: ofInt, e: Int) =>
    if (xs.contains(e)) {
      val a = xs.deleteFirst(e)
      a.size must_== (xs.size - 1)
      val i = xs.indexOf(e).get
      a.self must_=== (xs.take(i) ++ xs.drop(i + 1)).self
    } else {
      xs.deleteFirst(e) must_== xs
    }
  }

  val interleave = forAll { (xs: ofInt, ys: ofInt) =>
    val a = xs interleave ys
    (xs.length + ys.length) must_== a.length
    val min = math.min(xs.length, ys.length)

    xs.toList.zipWithIndex.forall { case (x, i) =>
      val index = if (i <= min) i * 2 else (min * 2) + i - min
      a.self(index) == x
    } must_== true

    ys.toList.zipWithIndex.forall { case (y, i) =>
      val index = if (i < min) (i * 2) + 1 else (min * 2) + i - min
      a.self(index) == y
    } must_== true
  }

  val intersperse = forAll { (xs: ofInt, x: Int) =>
    val a = xs.intersperse(x)
    a.size must_== (
      if (xs.self.isEmpty) 0 else (xs.size * 2) - 1
    )
    (1 until a.size by 2).forall(a.self(_) == x) must_== true
    val size0 = if (xs.self.isEmpty) 0 else xs.size - 1
    a.count(x == _) must_== (xs.count(x == _) + size0)
    xs.forall(a.contains) must_== true
  }

  val toList = forAll { (xs: ofInt) =>
    xs.toList must_== xs.self.toList
  }

  val toArray = forAll { (xs: ofInt) =>
    xs.toArray must_=== xs.self.toArray
  }
}
