package nobox

import org.scalacheck._
import Prop.forAll

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
}
