package nobox

import org.scalacheck._
import Prop.forAll

abstract class Test(name: String) extends Properties(name){
  def fail(message: String) =
    throw new AssertionError(message)

  implicit class AnyOps(actual: => Any) {
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
  }

}
