package nobox

object Benchmark {
  def time[A](action: => A): Long = {
    System.gc()
    System.runFinalization()
    val start = System.nanoTime
    val _ = action
    System.nanoTime - start
  }

  def exec[A](name: String, f1: => A, f2: => A){
    println(name)
    val x = time(f1)
    val y = time(f2)
    val a = 1000000.0
    println((x / a, y / a))
    val n = x.toDouble / y.toDouble
    if(n > 1){
      println(n)
    }else{
      println(Console.RED + n + Console.RESET)
    }
    println()
  }

  def main(args: Array[String]){
    val sizeOpt = args.headOption.flatMap(n => util.Try(n.toInt).toOption)
    val size = sizeOpt getOrElse 40000000
    val array1 = util.Random.shuffle(1 to size).toArray
    val array2 = new ofInt(array1)

    def _exec[A](name: String, f1: => A, f2: => A){
      val testAll = (args.size == 1 && sizeOpt.isDefined) || args.isEmpty
      if(args.contains(name) || testAll){
        exec(name, f1, f2)
      }
    }

    def benchmark(name: String, n: Double = 1.0)(f1: Array[Int] => Unit, f2: ofInt => Unit){
      val (a1, a2) = if(n != 1.0){
        val nn = (size * n).toInt
        (array1.take(nn), array2.take(nn))
      }else{
        (array1, array2)
      }
      _exec(name, f1(a1), f2(a2))
    }

    benchmark("foreach")(_.foreach(_ + 1), _.foreach(_ + 1))

    benchmark("map")(_.map(_ + 1), _.mapInt(_ + 1))

    benchmark("map")(_.map(_ + 1), _.map(_ + 1))

    benchmark("reverseMap")(_.reverseMap(_ + 1), _.reverseMapInt(_ + 1))

    benchmark("reverseMap")(_.reverse.map(_ + 1), _.reverseMapInt(_ + 1))

    benchmark("reverseMap")(_.reverseMap(_ + 1), _.reverseMap(_ + 1))

    _exec("reverseMap", array2.reverse.mapInt(_ + 1), array2.reverseMapInt(_ + 1))

    benchmark("exists")(_.exists(_ == -1), _.exists(_ == -1))

    benchmark("contains")(_.contains(-1), _.contains(-1))

    benchmark("filter")(_.filter(_ % 2 == 0), _.filter(_ % 2 == 0))

    benchmark("find")(_.find(_ == -1), _.find(_ == -1))

    benchmark("reverse")(_.reverse, _.reverse)

    benchmark("sorted")(_.sorted, _.sorted)

    benchmark("flatMap")(_.flatMap(n => Array(n, n, n)), _.flatMapInt(n => Array(n, n, n)))

    benchmark("flatMap")(_.flatMap(n => Array(n, n, n)), _.flatMap(n => Array(n, n, n)))

    benchmark("collectInt")(
      _.collect{case n if n > 10 => n + 1},
      _.collectInt{case n if n > 10 => n + 1}
    )

    benchmark("collect")(
      _.collect{case n if n > 10 => n + 1},
      _.collect{case n if n > 10 => n + 1}
    )

    benchmark("collectFirst")(
      _.collect{case 1 => 2},
      _.collectInt{case 1 => 2}
    )

    benchmark("takeWhile")(_.takeWhile(_ > 1), _.takeWhile(_ > 1))

    benchmark("take")(_.take(size / 2), _.take(size / 2))

    benchmark("takeRight")(_.takeRight(size / 2), _.takeRight(size / 2))

    benchmark("splitAt")(_.splitAt((size / 1.5).toInt), _.splitAt((size / 1.5).toInt))

    benchmark("sum")(_.sum, _.sum)

    benchmark("sumLong 1")(_.sum, _.sumLong)

    benchmark("sumLong 2")(_.foldLeft(0L)(_ + _), _.sumLong)

    benchmark("sumLong 3")(_.map(_.toLong).sum, _.sumLong)

    benchmark("product")(_.product, _.product)

    benchmark("productLong 1")(_.product, _.productLong)

    benchmark("productLong 2")(_.foldLeft(1L)(_ * _), _.productLong)

    benchmark("productLong 3")(_.map(_.toLong).product, _.productLong)

    benchmark("productDouble 1")(_.product, _.productDouble)

    benchmark("productDouble 2")(_.foldLeft(1: Double)(_ * _), _.productDouble)

    benchmark("productDouble 3")(_.map(_.toDouble).product, _.productDouble)

    benchmark("++")(a => a ++ a ++ a ++ a, a => a ++ a ++ a ++ a)

    benchmark("count")(_.count(_ % 10 != 0), _.count(_ % 10 != 0))

    benchmark("updated")(_.updated(size / 2, 0), _.updated(size / 2, 0))

    benchmark("drop")(_.drop(100), _.drop(100))

    benchmark("dropRight")(_.dropRight(100), _.dropRight(100))

    benchmark("slice")(_.slice(10, size - 10), _.slice(10, size - 10))

    benchmark("partition")(_.partition(_ > (size / 4)), _.partition(_ > (size / 4)))

    benchmark("reduceLeftOption")(_.reduceLeftOption(_ + _), _.reduceLeftOption(_ + _))

    benchmark("reduceRightOption")(_.reduceRightOption(_ + _), _.reduceRightOption(_ + _))

    benchmark("foldLeftInt")(_.foldLeft(0)(_ + _), _.foldLeftInt(0)(_ + _))

    benchmark("foldLeftAny")(_.foldLeft(0)(_ + _), _.foldLeftAny(0)(_ + _))

    benchmark("foldLeft")(_.foldLeft(0)(_ + _), _.foldLeft(0)(_ + _))

    benchmark("foldRightInt")(_.foldRight(0)(_ + _), _.foldRightInt(0)(_ + _))

    benchmark("foldRightAny")(_.foldRight(0)(_ + _), _.foldRightAny(0)(_ + _))

    benchmark("foldRight")(_.foldRight(0)(_ + _), _.foldRight(0)(_ + _))

    benchmark("indexOf")(_.indexOf(-1), _.indexOf(-1))

    benchmark("lastIndexOf")(_.lastIndexOf(-1), _.lastIndexOf(-1))

    {
      lazy val array3 = array1.clone
      lazy val array4 = new ofInt(array2.self.clone)
      benchmark("===")(_ sameElements array3, _ === array4)
    }

    benchmark("mkString", 0.2)(_ mkString ",", _ mkString ",")

    benchmark("tails", 0.0005)(_.tails.size, _.tails.size)

    benchmark("inits", 0.0005)(_.inits.size, _.inits.size)

    benchmark("tailOption")(_.tail, _.tailOption)

    benchmark("initOption")(_.init, _.initOption)

    List(50, 10000, 2000000).foreach{ n =>
      benchmark("grouped " + n, 0.2)(_.grouped(n).size, _.grouped(n).size)
    }

    benchmark("max")(_.max, _.max)

    benchmark("min")(_.min, _.min)

    benchmark("scanLeft")(_.scanLeft(0)(_ + _), _.scanLeft(0)(_ + _))

    benchmark("scanRight", 0.2)(_.scanRight(0)(_ + _), _.scanRight(0)(_ + _))

    benchmark("scanLeftInt")(_.scanLeft(0)(_ + _), _.scanLeftInt(0)(_ + _))

    benchmark("scanRightInt", 0.2)(_.scanRight(0)(_ + _), _.scanRightInt(0)(_ + _))

    benchmark("startsWith")(a => a.startsWith(a), a => a.startsWith(a.self))

    benchmark("endsWith")(a => a.endsWith(a), a => a.endsWith(a.self))

    benchmark("iterate")(_ => Array.iterate(0, size)(_ + 1), _ => ofInt.iterate(0, size)(_ + 1))

    benchmark("tabulate")(_ => Array.tabulate(size)(_ + 1), _ => ofInt.tabulate(size)(_ + 1))

    List(50, 10000, 2000000).foreach{ n =>
      benchmark("groupBy")(_.groupBy(_ % n), _.groupBy(_ % n))
    }

    List(50, 10000, 2000000).foreach{ n =>
      lazy val x = array2.sliding(n, n).map(_.self).toArray
      _exec("flatten", x.flatten, ofInt.flatten(x))
      _exec("flatten", Array.concat(x : _*), ofInt.flatten(x))
    }

    _exec("reverse_:::", array2.reverse ++ array2, array2 reverse_::: array2)
  }

}
