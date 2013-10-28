package nobox

object Eq {
  def apply[A](f: (A, A) => Boolean): Eq[A] = new Eq[A] {
    def equal(a: A, b: A) = f(a, b)
  }

  def natural[A]: NaturalEq[A] = new NaturalEq[A]

  implicit def ArrayEq[A](implicit A: Eq[A]): Eq[Array[A]] =
    Eq((a, b) => a.toSeq.corresponds(b)(A.equal(_, _)))

  implicit val ofIntEq: Eq[ofInt] = Eq(_ === _)

  implicit def ofRefEq[A <: AnyRef](implicit A: Eq[A]): Eq[ofRef[A]] =
    Eq((a, b) => a.self.corresponds(b.self)(A.equal(_, _)))

  implicit val bool = natural[Boolean]
  implicit val int = natural[Int]
  implicit val long = natural[Long]
  implicit val float = natural[Float]
  implicit val double = natural[Double]
  implicit val string = natural[String]

  implicit def option[A: NaturalEq]: NaturalEq[Option[A]] = natural[Option[A]]
}

final class NaturalEq[A] extends Eq[A] {
  def equal(a: A, b: A) = a == b
}

trait Eq[A] {
  def equal(a: A, b: A): Boolean

}



