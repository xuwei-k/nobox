package nobox

import scalaprops._
import scalaprops.Property.forAll

object TestRef extends TestBase {

  val collect1 = forAll { (a: ofRef[Integer], pf: PartialFunction[Integer, Long]) =>
    a.collectLong(pf).self must_=== a.self.collect(pf)
  }

  val collectFirst1 = forAll { (a: ofRef[Integer], pf: PartialFunction[Integer, Long]) =>
    a.collectFirstLong(pf) must_== a.self.collectFirst(pf)
  }

  val exists = forAll { (a: ofRef[Integer], f: Integer => Boolean) =>
    a.exists(f) must_== a.self.exists(f)
  }

  val filter = forAll { (a: ofRef[Integer], f: Integer => Boolean)  =>
    a.filter(f).self must_=== a.self.filter(f)
  }

  val find = forAll { (a: ofRef[Integer], f: Integer => Boolean)  =>
    a.find(f) must_== a.self.find(f)
  }

  val flatMap = forAll { a: ofRef[Integer] =>
    val f: Integer => Array[Integer] = {i: Integer => Array[Integer](i, i + 10)}
    a.flatMap(f).self must_=== a.self.flatMap(x => f(x).toList)
  }

  val forall = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => 5 < i }
    a.forall(f) must_== a.self.forall(f)
  }

  val mapInt = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i - 1 }
    a.mapInt(f).self must_=== a.self.map(f)
  }

  val map = forAll { (a: ofRef[Integer], f: Integer => List[Boolean])  =>
    a.map(f) must_=== a.self.map(f)
  }

  val reverseMapInt = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i * 2 }
    a.reverseMapInt(f).self must_=== a.self.reverseMap(f).toArray
  }

  val reverseMap = forAll { (a: ofRef[Integer], f2: Integer => Byte) =>
    val f1 = (_:Integer).toString
    a.reverseMap(f1).self must_=== a.self.reverseMap(f1).toArray
    a.reverseMap(f2).self must_=== a.self.reverseMap(f2).toArray
  }

  val `reverse.reverse` = forAll { a: ofRef[Integer] =>
    a.reverse.reverse === a
  }

  val reverse = forAll { a: ofRef[Integer] =>
    a.reverse.self must_=== a.self.reverse
  }

  val take = forAll { (a: ofRef[Integer], n: Int) =>
    a.take(n).self must_=== a.self.take(n)
  }

  val takeWhile = forAll { (a: ofRef[Integer], f: Integer => Boolean) =>
    a.takeWhile(f).self must_=== a.self.takeWhile(f)
  }

  val takeRight = forAll { (a: ofRef[Integer], n: Int) =>
    a.takeRight(n).self must_=== a.self.toList.takeRight(n).toArray
  }

  val count = forAll { (a: ofRef[Integer], f: Integer => Boolean) =>
    a.count(f) must_== a.self.count(f)
  }

  val drop = forAll { (a: ofRef[Integer], n: Integer) =>
    a.drop(n).self must_=== a.self.drop(n)
  }

  val dropWhile = forAll { (a: ofRef[Integer], f: Integer => Boolean) =>
    a.dropWhile(f).self must_=== a.self.dropWhile(f)
  }

  val dropRight = forAll { (a: ofRef[Integer], n: Integer) =>
    a.dropRight(n).self must_=== a.self.toList.dropRight(n).toArray
  }

  val contains = forAll { (a: ofRef[Integer], n: Integer) =>
    a.contains(n) must_== a.self.contains(n)
  }

  val splitAt = forAll { (a: ofRef[Integer], n: Integer) =>
    val (x1, x2) = a.splitAt(n)
    val (y1, y2) = a.self.splitAt(n)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val span = forAll { (a: ofRef[Integer], f: Integer => Boolean) =>
    val (x1, x2) = a.span(f)
    val (y1, y2) = a.self.span(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val ++ = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    (a ++ b).self must_=== (a.self ++ b.self)
  }

  val partition = forAll { a: ofRef[Integer] =>
    val f = {i: Integer => i > 0}
    val (x1, x2) = a.partition(f)
    val (y1, y2) = a.self.partition(f)
    (x1.self must_=== y1) && (x2.self must_=== y2)
  }

  val reverse_::: = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    (a reverse_::: b).self must_=== (a.self.toList reverse_::: b.self.toList).toArray
  }

  val updated = forAll { (a: ofRef[Integer], index: Int, elem: Integer) =>
    if(0 <= index && index < a.size)
      a.updated(index, elem).self must_=== a.self.updated(index, elem)
    else
      a.updated(index, elem).mustThrowA[nobox.Platform.IndexOutOfBoundsError]
  }

  val sum = {
    implicit val bigIntGen = Gen[Long].map(BigInt(_))
    forAll { a: ofRef[BigInt] =>
      a.sum must_== a.self.sum
    }
  }

  // TODO product

  val sorted = forAll { a: ofRef[Integer] =>
    val o = implicitly[Ordering[Integer]]
    a.sorted.self must_=== a.self.sorted
    a.sorted(o.reverse).self must_=== a.self.sorted(o.reverse)
  }

  val slice = forAll { (a: ofRef[Integer], from: Int, until: Int) =>
    a.slice(from, until).self must_=== a.self.slice(from, until)
  }

  val reduceLeftOption = forAll { a: ofRef[Integer] =>
    a.reduceLeftOption(_ - _) must_== a.self.reduceLeftOption(_ - _)
  }

  val reduceRightOption = forAll { a: ofRef[Integer] =>
    a.reduceRightOption(_ - _) must_== a.self.reduceRightOption(_ - _)
  }

  val foldLeftInt = forAll { (a: ofRef[Integer], z: Int) =>
    a.foldLeftInt(z)(_ - _) must_== a.self.foldLeft(z)(_ - _)
  }

  val foldLeftRef = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.foldLeftRef(z.toVector)(_ :+ _) must_== a.self.foldLeft(z.toVector)(_ :+ _)
  }

  val foldRightLong = forAll { (a: ofRef[Integer], z: Long) =>
    a.foldRightLong(z)(_ - _) must_== a.self.foldRight(z)(_ - _)
  }

  val foldRightRef = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.foldRightRef(z)(_ :: _) must_== a.self.foldRight(z)(_ :: _)
  }

  val indexOf = forAll { (a: ofRef[Integer], z: Int) =>
    a.indexOf(z) must_== Option(a.self.indexOf(z)).filter(_ >= 0)
  }

  val lastIndexOf = forAll { (a: ofRef[Integer], z: Int) =>
    a.lastIndexOf(z) must_== Option(a.self.lastIndexOf(z)).filter(_ >= 0)
  }

  val mkString = forAll { (a: ofRef[Integer], start: String, sep: String, end: String) =>
    a.mkString(start, sep, end) must_== a.self.mkString(start, sep, end)
  }

  val tails = forAll { (a: ofRef[Integer]) =>
    a.tails.map(_.self.toSeq).toList must_== a.self.tails.map(_.toSeq).toList
  }

  val inits = forAll { (a: ofRef[Integer]) =>
    a.inits.map(_.self.toSeq).toList must_== a.self.inits.map(_.toSeq).toList
  }

  val tailOption = forAll { a: ofRef[Integer] =>
    a.tailOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.tail.toSeq)
    )
  }

  val initOption = forAll { a: ofRef[Integer] =>
    a.initOption.map(_.self.toSeq) must_== (
      if(a.self.isEmpty) None
      else Some(a.self.init.toSeq)
    )
  }

  val grouped = forAll { (a: ofRef[Integer], n: Int) =>
    if(n > 0){
      a.grouped(n).map(_.self.toSeq).toList must_== a.self.grouped(n).map(_.toSeq).toList
    }else{
      a.grouped(n).mustThrowA[IllegalArgumentException]
    }
  }

  val sliding = forAll { (a: ofRef[Integer], size: Int, step: Int) =>
    if(size > 0 && step > 0){
      a.sliding(size, step).map(_.self.toSeq).toList must_== (
        a.self.sliding(size, step).map(_.toSeq).toList
      )
    }else{
      a.sliding(size, step).mustThrowA[IllegalArgumentException]
    }
  }

  val max = forAll { a: ofRef[Integer] =>
    if(a.self.isEmpty){
      a.max must_== None
    }else{
      a.max must_== Option(a.self.max)
    }
  }

  val min = forAll { a: ofRef[Integer] =>
    if(a.self.isEmpty){
      a.min must_== None
    }else{
      a.min must_== Option(a.self.min)
    }
  }

  val minmax = forAll { a: ofRef[Integer] =>
    if(a.self.isEmpty){
      a.minmax must_== None
    }else{
      a.minmax must_== Option((a.self.min, a.self.max))
    }
  }

  val scanLeftInt = forAll { (a: ofRef[Integer], z: Int) =>
    a.scanLeftInt(z)(_ - _).self.toList must_== a.self.scanLeft(z)(_ - _).toList
  }

  val scanRightInt = forAll { (a: ofRef[Integer], z: Int) =>
    a.scanRightInt(z)(_ - _).self.toList must_== a.self.scanRight(z)(_ - _).toList
  }

  val scanLeft = forAll { (a: ofRef[Integer], z: List[Integer]) =>
    a.scanLeft(z)(_ :+ _).self.toList must_== a.self.scanLeft(z)(_ :+ _).toList
  }

  val scanRight = forAll { (a: ofRef[Integer], z: List[Int]) =>
    a.scanRight(z)(_ :: _).self.toList must_== a.self.scanRight(z)(_ :: _).toList
  }

  val scanLeft1 = forAll { a: ofRef[Integer] =>
    a.scanLeft1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.tail.scanLeft(a.self.head)(_ - _).toList
    )
  }

  val scanRight1 = forAll { a: ofRef[Integer] =>
    a.scanRight1(_ - _).self.toList must_== (
      if(a.self.isEmpty) List()
      else a.self.init.scanRight(a.self.last)(_ - _).toList
    )
  }

  val startsWith = forAll { (a: ofRef[Integer], b: ofRef[Integer], n: Int) =>
    if(n >= 0){
      a.startsWith(b.self, n) must_== a.self.startsWith(b.self, n)
    }else{
      a.startsWith(b.self, n).mustThrowA[IllegalArgumentException]
    }
  }

  val endsWith = forAll { (a: ofRef[Integer], b: ofRef[Integer]) =>
    a.endsWith(b.self) must_== a.self.endsWith(b.self)
  }

  val iterate = forAll { size: UInt8 =>
    ofRef.iterate("0", size)(a => (a.toInt + 1).toString).self must_=== Array.iterate("0", size)(a => (a.toInt + 1).toString)
  }

  val tabulate = forAll { size: UInt8 =>
    ofRef.tabulate(size)(_.toString).self must_=== Array.tabulate(size)(_.toString)
  }

  val flatten1 = forAll { xs: Array[Array[Integer]] =>
    xs.flatten must_=== ofRef.flatten(xs).self
    Array.concat(xs: _*) must_=== ofRef.flatten(xs).self
  }

  val flatten2 = forAll { xs: ofRef[Array[String]] =>
    xs.flatten must_=== xs.self.flatten
  }.toProperties((), Param.maxSize(5))

  val groupBy = forAll { (a: ofRef[Integer], x: PInt8) =>
    a.self.groupBy(_ % x).map{case (k, v) => k -> v.toList} must_== (
      a.groupBy(_ % x).map{case (k, v) => k -> v.self.toList}
    )
  }

  val `for comprehension` = forAll { (xs: ofRef[Integer], ys: ofByte) =>
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

  val testToString = forAll { xs: ofRef[Integer] =>
    xs.toString must_== xs.mkString("ofRef(", ", ", ")")
  }

  val interleave = forAll { (xs: ofRef[String], ys: ofRef[String]) =>
    val a = xs interleave ys
    (xs.length + ys.length) must_== a.length
    val min = math.min(xs.length, ys.length)

    xs.toList.zipWithIndex.forall{ case (x, i) =>
      val index = if(i <= min) i * 2 else (min * 2) + i - min
      a.self(index) == x
    } must_== true

    ys.toList.zipWithIndex.forall{ case (y, i) =>
      val index = if(i < min) (i * 2) + 1 else (min * 2) + i - min
      a.self(index) == y
    } must_== true
  }

  val intersperse = forAll { (xs: ofRef[String], x: String) =>
    val a = xs.intersperse(x)
    a.size must_== (
      if(xs.self.isEmpty) 0 else (xs.size * 2) - 1
    )
    (1 until a.size by 2).forall(a.self(_) == x) must_== true
    val size0 = if(xs.self.isEmpty) 0 else xs.size - 1
    a.count(x == _) must_== (xs.count(x == _) + size0)
    xs.forall(a.contains) must_== true
  }

}
