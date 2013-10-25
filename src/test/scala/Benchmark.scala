package nobox

trait Benchmark {
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
    println((x / 1000000.0, y / 1000000.0))
    val n = x.toDouble / y.toDouble
    if(n > 1){
      println(n)
    }else{
      println(Console.RED + n + Console.RESET)
    }
    println()
  }

}

object Benchmark {
  def main(args: Array[String]){
    BenchmarkArray.run()
    BenchmarkList.run()
  }
}

object BenchmarkArray extends Benchmark{

  val defaultSize = 40000000

  def main(args: Array[String]){
    val size = args.headOption.flatMap(n => util.Try(n.toInt).toOption) getOrElse defaultSize

    run(size)
  }

  def run(size: Int = defaultSize){
    val array1 = util.Random.shuffle(1 to size).toArray
    val array2 = new ofInt(array1)

    val list1 = array1.toList

    def benchmark(name: String)(f1: Array[Int] => Unit, f2: ofInt => Unit){
      exec(name, f1(array1), f2(array2))
    }

    benchmark("map")(_.map(_ + 1), _.mapInt(_ + 1))

    benchmark("exists")(_.exists(_ == -1), _.exists(_ == -1))

    benchmark("contains")(_.contains(-1), _.contains(-1))

    benchmark("filter")(_.filter(_ % 2 == 0), _.filter(_ % 2 == 0))

    benchmark("find")(_.find(_ == -1), _.find(_ == -1))

    benchmark("reverse")(_.reverse, _.reverse)

    benchmark("sorted")(_.sorted, _.sorted)

    benchmark("flatMap")(_.flatMap(n => Array(n, n, n)), _.flatMapInt(n => Array(n, n, n)))

    benchmark("collect")(
      _.collect{case n if n > 10 => n + 1},
      _.collectInt{case n if n > 10 => n + 1}
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

    benchmark("++")(a => a ++ a, a => a ++ a)

    benchmark("count")(_.count(_ % 10 != 0), _.count(_ % 10 != 0))

    benchmark("updated")(_.updated(size / 2, 0), _.updated(size / 2, 0))

    benchmark("drop")(_.drop(100), _.drop(100))

    benchmark("dropRight")(_.dropRight(100), _.dropRight(100))

    benchmark("slice")(_.slice(10, size - 10), _.slice(10, size - 10))

    benchmark("partition")(_.partition(_ > (size / 4)), _.partition(_ > (size / 4)))

    benchmark("reduceLeftOption")(_.reduceLeftOption(_ + _), _.reduceLeftOption(_ + _))

    benchmark("reduceRightOption")(_.reduceRightOption(_ + _), _.reduceRightOption(_ + _))

    benchmark("foldLeftInt")(_.foldLeft(0)(_ + _), _.foldLeftInt(0)(_ + _))

    benchmark("foldLeft")(_.foldLeft(0)(_ + _), _.foldLeft(0)(_ + _))

    benchmark("foldRightInt")(_.foldRight(0)(_ + _), _.foldRightInt(0)(_ + _))

    benchmark("foldRight")(_.foldRight(0)(_ + _), _.foldRight(0)(_ + _))

    exec("reverse_:::", array2.reverse ++ array2, array2 reverse_::: array2)
  }
  
}

object BenchmarkList extends Benchmark{

  val defaultSize = 40000000

  def main(args: Array[String]){
    val size = args.headOption.flatMap(n => util.Try(n.toInt).toOption) getOrElse defaultSize
    run(size)
  }

  def run(size: Int = defaultSize){
    val list1 = util.Random.shuffle(1 to size).toList
    val list2 = IntList(list1: _*)

    def benchmarkList(name: String)(f1: List[Int] => Unit, f2: IntList => Unit){
      exec(name, f1(list1), f2(list2))
    }

    benchmarkList("map")(_.map(_ + 1), _.mapInt(_ + 1))

    benchmarkList("mapLong")(_.map[Long, List[Long]](_ + 1L), _.mapLong(_ + 1L))

    benchmarkList("reverse")(_.reverse, _.reverse)

    benchmarkList("foldLeft")(_.foldLeft(0)(_ + _), _.foldLeft(0)(_ + _))

    benchmarkList("foldRight")(_.foldRight(0)(_ + _), _.foldRight(0)(_ + _))

    benchmarkList("length")(_.length, _.length)

    benchmarkList("toArray")(_.toArray, _.toArray)
  }
}
