package nobox

import scalaprops.Scalaprops

abstract class TestBase extends Scalaprops {
  import scalaprops.Gen

  implicit final def ofRefGen[A <: AnyRef : Gen: reflect.ClassTag]: Gen[ofRef[A]] =
    Gen[List[A]].map(xs => ofRef[A](xs: _*))

  protected final def fail(message: String) =
    throw new AssertionError(message)

  implicit final class AnyOps[A](actual: => A) {
    def mustThrowA[T <: Throwable](implicit man: reflect.ClassTag[T]): Boolean = {
      val erasedClass = man.runtimeClass
      try {
        actual
        fail("no exception thrown, expected " + erasedClass)
      } catch {
        case ex: Throwable =>
          if (!erasedClass.isInstance(ex))
            fail("wrong exception thrown, expected: " + erasedClass + " got: " + ex)
          else
            true
      }
    }

    def must_==(that: A): Boolean = {
      val self = actual
      if(self == that) true
      else {
        fail(self.toString + " is not equal to " + that)
      }
    }
  }

  implicit final class ArrayOps[A](val self: Array[A]) {
    def must_===(that: Array[A]): Boolean = {
      if(self sameElements that) true
      else {
        val msg = self.mkString("Array(",",",")") + " is not equal " + that.mkString("Array(",",",")")
        fail(msg)
      }
    }
  }

  implicit val stringGen: Gen[String] = scalaprops.Gen.alphaNumString

  protected[this] implicit val onInt1Gen: Gen[ofInt1] =
    Gen[(Int, Array[Int])].map{ case (h, t) => ofInt1(h, t: _*) }

  protected[this] implicit val onByteGen: Gen[ofByte] =
    Gen[Array[Byte]].map{ xs => ofByte(xs: _*) }

  protected[this] implicit val onIntGen: Gen[ofInt] =
    Gen[Array[Int]].map{ xs => ofInt(xs: _*) }

  protected[this] implicit val onFloatGen: Gen[ofFloat] = {
    implicit val scalaFloatGen: Gen[Float] =
      Gen[Int].map { n =>
        java.lang.Float.intBitsToFloat(n) match {
          case x if x.isNaN => n
          case x => x
        }
      }

    Gen[Array[Float]].map{ xs => ofFloat(xs: _*) }
  }

  type Tagged[T] = {type Tag = T}

  type @@[+T, Tag] = T with Tagged[Tag]

  object Tag {
    def apply[A, T](a: A): A @@ T = a.asInstanceOf[A @@ T]

    def subst[F[_], A, T](fa: F[A]): F[A @@ T] = fa.asInstanceOf[F[A @@ T]]
  }

  sealed trait Unsigned8
  /** unsigned 8bit integer. 0 to 255 */
  type UInt8 = Int @@ Unsigned8

  sealed trait Positive8
  /** positive 8bit integer. 1 to 256 */
  type PInt8 = Int @@ Positive8

  implicit val uIntGen: Gen[UInt8] =
    Tag.subst(Gen.choose(0, 255))

  implicit val pIntGen: Gen[PInt8] =
    Tag.subst(Gen.choose(1, 255))

}
