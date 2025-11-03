package nobox

import scalaprops.Property.forAll

object TestTaggedType extends TestBase {

  val uInt8 = forAll { (a: UInt8) =>
    (0 <= a) && (a < 256)
  }

  val pInt8 = forAll { (a: PInt8) =>
    (0 < a) && (a <= 256)
  }

}
