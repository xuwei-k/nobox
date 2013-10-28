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

    benchmark("scanRight")(
      _.scanRight(List[Integer](0))(_ :: _),
      _.scanRight(List[Integer](0))(_ :: _)
    )
  }

}
