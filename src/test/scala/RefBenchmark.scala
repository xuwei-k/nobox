package nobox

object RefBenchmark extends Benchmark{

  def defaultSize = 10000000
  type Array1 = Array[Integer]
  type Array2 = ofRef[Integer]
  def createSampleArray(size: Int) = {
    val array1 = util.Random.shuffle(1 to size).map(Integer.valueOf).toArray
    val array2 = new ofRef(array1)
    (array1, array2)
  }

  def run(){
    val a = args
    import a._

    benchmark("foreach")(_.foreach(_ + 1), _.foreach(_ + 1))

    benchmark("map")(_.map(_ + 1), _.map(_ + 1))

    benchmark("reverseMap")(_.reverse.map(_ + 1), _.reverseMap(_ + 1))

    benchmark("reverseMap")(_.reverseMap(_ + 1), _.reverseMap(_ + 1))

    benchmark("exists")(_.exists(_ == -1), _.exists(_ == -1))

    benchmark("contains")(_.contains(-1), _.contains(-1))

    benchmark("filter")(_.filter(_ % 2 == 0), _.filter(_ % 2 == 0))

    benchmark("find")(_.find(_ == -1), _.find(_ == -1))

    benchmark("reverse")(_.reverse, _.reverse)

    benchmark("flatMap")(_.flatMap(n => Array(n, n, n)), _.flatMap(n => Array(n, n, n)))

    benchmark("collect")(
      _.collect{case n if n > 10 => n + 1},
      _.collect{case n if n > 10 => n + 1}
    )

    val IntOne = Integer.valueOf(1)

    benchmark("collectFirst")(
      _.collectFirst{case IntOne => 2},
      _.collectFirstInt{case IntOne => 2}
    )

    benchmark("takeWhile")(_.takeWhile(_ > 1), _.takeWhile(_ > 1))

    benchmark("take")(_.take(size / 2), _.take(size / 2))

    benchmark("takeRight")(_.takeRight(size / 2), _.takeRight(size / 2))

    benchmark("splitAt")(_.splitAt((size / 1.5).toInt), _.splitAt((size / 1.5).toInt))

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

    benchmark("foldLeftRef")(_.foldLeft(0)(_ + _), _.foldLeftRef(0)(_ + _))

    benchmark("foldLeft")(_.foldLeft(0)(_ + _), _.foldLeft(0)(_ + _))

    benchmark("foldRightInt")(_.foldRight(0)(_ + _), _.foldRightInt(0)(_ + _))

    benchmark("foldRightRef")(_.foldRight(0)(_ + _), _.foldRightRef(0)(_ + _))

    benchmark("foldRight")(_.foldRight(0)(_ + _), _.foldRight(0)(_ + _))

    benchmark("indexOf")(_.indexOf(-1), _.indexOf(-1))

    benchmark("lastIndexOf")(_.lastIndexOf(-1), _.lastIndexOf(-1))

    {
      lazy val array3 = array1.clone
      lazy val array4 = new ofRef(array2.self.clone)
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

    benchmark("scanLeft")(_.scanLeft(0)(_ + _), _.scanLeft(0)(_ + _))

    benchmark("scanRight", 0.2)(_.scanRight(List[Integer]())(_ :: _), _.scanRight(List[Integer]())(_ :: _))

    benchmark("startsWith")(a => a.startsWith(a), a => a.startsWith(a.self))

    benchmark("endsWith")(a => a.endsWith(a), a => a.endsWith(a.self))

    List(50, 10000, 2000000).foreach{ n =>
      benchmark("groupBy")(_.groupBy(_ % n), _.groupBy(_ % n))
    }

    List(50, 10000, 2000000).foreach{ n =>
      val x: Array[Array[Integer]] = array2.sliding(n, n).map(_.self).toArray
      val a: ofRef[Array[Int]] = new ofRef(x.map(_.map(_.toInt)))
      _exec("flatten", x.flatten, ofRef.flatten(x))
      _exec("flatten", Array.concat(x : _*), ofRef.flatten(x))
      _exec("flatten", a.self.flatten, a.flatten)
      _exec("flatten", x.flatten, new ofRef(x).flatten)
    }

    _exec("reverse_:::", array2.reverse ++ array2, array2 reverse_::: array2)

    benchmark("max")(_.max, _.max)

    benchmark("min")(_.min, _.min)

    {
      val array: ofRef[BigInt] = new ofRef((BigInt(1) to size).toArray)
      _exec("sum", array.self.sum, array.sum)
    }

    _exec("fill", Array.fill(size)(""), ofRef.fill(size)(""))

    _exec("fillAll", Array.fill(size)(""), ofRef.fillAll(size)(""))
  }

}
