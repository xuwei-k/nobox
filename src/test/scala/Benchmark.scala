package nobox

/* typeclass */
trait Take[A]{
  def resize(a: A, n: Int): A
}

object Take{
  private def apply[A](f: (A, Int) => A): Take[A] = new Take[A]{
    def resize(a: A, n: Int) = f(a, n)
  }

  implicit def array[A]: Take[Array[A]] = Take(_ take _)

  implicit val ofInt: Take[ofInt] = Take(_ take _)

  implicit def ofRef[A <: AnyRef]: Take[ofRef[A]] = Take(_ take _)
}

trait Benchmark {

  final case class Args(size: Int, names: Set[String], array1: Array1, array2: Array2) {
    def test(name: String): Boolean = names(name) || names.isEmpty

    override def toString = (size, names, array1.getClass, array2.getClass).toString
  }

  def time[A](action: => A): Long = {
    System.gc()
    System.runFinalization()
    val start = System.nanoTime
    val _ = action
    System.nanoTime - start
  }

  def exec[A](name: String, f1: => A, f2: => A): Unit = {
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

  def parseArg(args: Array[String]): Args = {
    val size = args.headOption.flatMap(n => util.Try(n.toInt).toOption).getOrElse(defaultSize)
    val names = args.filter(_ != size.toString).toSet
    val (array1, array2) = createSampleArray(size)
    Args(size, names, array1, array2)
  }

  def defaultSize: Int

  type Array1
  type Array2

  def createSampleArray(size: Int): (Array1, Array2)

  def _exec[A](name: String, f1: => A, f2: => A): Unit = {
    if(args.test(name)){
      exec(name, f1, f2)
    }
  }

  def benchmark(name: String, n: Double = 1.0)(f1: Array1 => Unit, f2: Array2 => Unit)(
    implicit A1: Take[Array1], A2: Take[Array2] ): Unit = {
    val (a1, a2) = if(n != 1.0){
      val nn = (args.size * n).toInt
      (A1.resize(args.array1, nn), A2.resize(args.array2, nn))
    }else{
      (args.array1, args.array2)
    }
    _exec(name, f1(a1), f2(a2))
  }

  def run(): Unit

  private[this] var _args: Args = _

  def args: Args = _args

  def main(a: Array[String]): Unit = {
    _args = parseArg(a)
    println(args)
    run()
  }

}
