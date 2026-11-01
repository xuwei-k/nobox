package nobox

import scalaprops.Property.forAll

object TestJVM_JS extends TestBase {

  property("flattenRef") = forAll { (xs: ofRef[Array[Int]]) =>
    xs.flatten must_=== xs.self.flatten
  }

}
