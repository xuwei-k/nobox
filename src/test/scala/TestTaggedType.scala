package nobox

import org.scalacheck.Prop.forAll

object TestTaggedType extends TestBase("TaggedType") {

  property("UInt8") = forAll { a: UInt8 =>
    (0 <= a) && (a < 256)
  }

  property("PInt8") = forAll { a: PInt8 =>
    (0 < a) && (a <= 256)
  }

}

