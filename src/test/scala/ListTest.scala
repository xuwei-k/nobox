package nobox

import org.scalacheck._
import Prop.forAll

object ListTest extends Test("list"){

  implicit val IntListArb: Arbitrary[IntList] =
    Arbitrary(implicitly[Arbitrary[Array[Int]]].arbitrary.map(array => IntList(array: _*)))

  val pf: PartialFunction[Int, Long] = {case i: Int if i % 3 == 0 => i + 1}
  val f: Int => Boolean = {i: Int => 5 < i && i < 10 }

  property("map") = forAll { a: IntList =>
    a.mapInt(_ + 1).toList == a.toList.map(_ + 1)
  }

  property("headOption") = forAll { a: IntList =>
    a.headOption == a.toList.headOption
  }

  property("tailOption") = forAll { a: IntList =>
    if(a.isEmpty)
      a.tailOption == None
    else
      a.tailOption.map(_.toList) == Some(a.toList.tail)
  }

  property("reverse.reverse") = forAll { a: IntList =>
    a.reverse.reverse === a
  }

  property("reverse") = forAll { a: IntList =>
    a.reverse.toList == a.toList.reverse
  }

  property("length") = forAll { a: IntList =>
    a.length == a.toList.length
  }

  property("isEmpty") = forAll { a: IntList =>
    a.isEmpty == a.toList.isEmpty
  }

  property("toList") = forAll { a: IntList =>
    IntList(a.toList: _*) === a
  }
}
