package nobox

object RefBenchmark extends Benchmark{

  def defaultSize = 5000000
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

    benchmark("scanRight")(
      _.scanRight(List[Integer](0))(_ :: _),
      _.scanRight(List[Integer](0))(_ :: _)
    )
  }

}
