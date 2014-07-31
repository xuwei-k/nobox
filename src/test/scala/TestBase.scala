package nobox

import org.scalacheck._

abstract class TestBase(name: String) extends Properties(name){
  def fail(message: String) =
    throw new AssertionError(message)

  implicit class AnyOps[A](actual: => A) {
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
        fail(self + " is not equal to " + that)
      }
    }
  }

  implicit class ArrayOps[A](val self: Array[A]) {
    def must_===(that: Array[A]): Boolean = {
      if(self sameElements that) true
      else {
        val msg = self.mkString("Array(",",",")") + " is not equal " + that.mkString("Array(",",",")")
        fail(msg)
      }
    }
  }

  implicit val ofIntArb: Arbitrary[ofInt] =
    Arbitrary(implicitly[Arbitrary[Array[Int]]].arbitrary.map(array => ofInt(array: _*)))

  implicit val ofByteArb: Arbitrary[ofByte] =
    Arbitrary(implicitly[Arbitrary[Array[Byte]]].arbitrary.map(array => ofByte(array: _*)))

  implicit val ofFloatArb: Arbitrary[ofFloat] =
    Arbitrary(implicitly[Arbitrary[Array[Float]]].arbitrary.map(array => ofFloat(array: _*)))

  implicit def ofRefArb[A <: AnyRef : reflect.ClassTag](implicit A: Arbitrary[A]): Arbitrary[ofRef[A]] =
    Arbitrary(implicitly[Arbitrary[List[A]]].arbitrary.map(array => ofRef[A](array: _*)))

  implicit def javaLangIntegerArb: Arbitrary[Integer] =
    Arbitrary(implicitly[Arbitrary[Int]].arbitrary.map(Integer.valueOf(_)))

  type Tagged[T] = {type Tag = T}

  type @@[+T, Tag] = T with Tagged[Tag]

  object Tag {
    def apply[A, T](a: A): A @@ T = a.asInstanceOf[A @@ T]
  }

  sealed trait Unsigned8
  /** unsigned 8bit integer. 0 to 255 */
  type UInt8 = Int @@ Unsigned8

  sealed trait Positive8
  /** positive 8bit integer. 1 to 256 */
  type PInt8 = Int @@ Positive8

  implicit val UInt8Arb: Arbitrary[UInt8] =
    Arbitrary(implicitly[Arbitrary[Byte]].arbitrary.map(byte => Tag[Int, Unsigned8](byte + 128)))

  implicit val PInt8Arb: Arbitrary[PInt8] =
    Arbitrary(implicitly[Arbitrary[Byte]].arbitrary.map(byte => Tag[Int, Positive8](byte + 129)))
}

