package nobox

import Generate.list
import Generate.withRef

object SpecializedMethods {

  private final val collectIsSlow = s"""
  /**
   * can not avoid boxing `PartialFunction#applyOrElse`
   * use `.withFilter(predicate).map(f)` instead
   */"""

  def apply(a: Type): String = {

    val map0: String = {

      val cases: String = list.map { b =>
        s"      case ClassTag.$b => map$b(f.asInstanceOf[$a => $b]).self"
      }.mkString("\n")

      s"""
  def map[A](f: $a => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.map(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val flatMap0: String = {

      val cases: String = list.map { b =>
        s"      case ClassTag.$b => flatMap$b(f.asInstanceOf[$a => Array[$b]]).self"
      }.mkString("\n")

      s"""
  def flatMap[A](f: $a => Array[A])(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.flatMap(x => f(x))
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val reverseMap0: String = {

      val cases: String = list.map { b =>
        s"      case ClassTag.$b => reverseMap$b(f.asInstanceOf[$a => $b]).self"
      }.mkString("\n")

      s"""
  def reverseMap[A](f: $a => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.reverseIterator.map(f).toArray
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val collect0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => collect$b(f.asInstanceOf[PartialFunction[$a, $b]]).self"
      }.mkString("\n")

      s"""
  $collectIsSlow
  def collect[A](f: PartialFunction[$a, A])(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.collect(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val collectFirst0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => collectFirst$b(f.asInstanceOf[PartialFunction[$a, $b]])"
      }.mkString("\n")

      s"""
  def collectFirst[A](f: PartialFunction[$a, A])(implicit A: ClassTag[A]): Option[A] = {
    (A match {
$cases
      case _ => self.collectFirst(f)
    }).asInstanceOf[Option[A]]
  }
"""
    }

    val foldLeft0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => foldLeft$b(z.asInstanceOf[$b])(f.asInstanceOf[($b, $a) => $b])"
      }.mkString("\n")

      s"""
  def foldLeft[A](z: A)(f: (A, $a) => A)(implicit A: ClassTag[A]): A = {
    (A match {
$cases
      case _ => self.foldLeft(z)(f)
    }).asInstanceOf[A]
  }
"""
    }

    val foldRight0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => foldRight$b(z.asInstanceOf[$b])(f.asInstanceOf[($a, $b) => $b])"
      }.mkString("\n")

      s"""
  def foldRight[A](z: A)(f: ($a, A) => A)(implicit A: ClassTag[A]): A = {
    (A match {
$cases
      case _ => self.foldRight(z)(f)
    }).asInstanceOf[A]
  }
"""
    }

    val scanLeft0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => scanLeft$b(z.asInstanceOf[$b])(f.asInstanceOf[($b, $a) => $b]).self"
      }.mkString("\n")

      s"""
  def scanLeft[A](z: A)(f: (A, $a) => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.scanLeft(z)(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val scanRight0: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => scanRight$b(z.asInstanceOf[$b])(f.asInstanceOf[($a, $b) => $b]).self"
      }.mkString("\n")

      s"""
  def scanRight[A](z: A)(f: ($a, A) => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.scanRight(z)(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val foldMapLeft10: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => foldMapLeft1$b(z.asInstanceOf[$a => $b])(f.asInstanceOf[($b, $a) => $b])"
      }.mkString("\n")

      s"""
  def foldMapLeft1[A](z: $a => A)(f: (A, $a) => A)(implicit A: ClassTag[A]): Option[A] = {
    (A match {
$cases
      case _ => foldMapLeft1Ref(z)(f)
    }).asInstanceOf[Option[A]]
  }
"""
    }

    val foldMapRight10: String = {
      val cases: String = list.map { b =>
        s"      case ClassTag.$b => foldMapRight1$b(z.asInstanceOf[$a => $b])(f.asInstanceOf[($a, $b) => $b])"
      }.mkString("\n")

      s"""
  def foldMapRight1[A](z: $a => A)(f: ($a, A) => A)(implicit A: ClassTag[A]): Option[A] = {
    (A match {
$cases
      case _ => foldMapRight1Ref(z)(f)
    }).asInstanceOf[Option[A]]
  }
"""
    }

    val map: Type => String = { b =>
      import b.*
      s"""
  def map$yWithTag(f: $a => $y): of$tparamy = {
    val array = new Array[$y](self.length)
    var i = 0
    while(i < self.length){
      array(i) = f(self(i))
      i += 1
    }
    new of$tparamy(array)
  }
"""
    }

    val reverseMap: Type => String = { b =>
      import b.*
      s"""
  def reverseMap$yWithTag(f: $a => $y): of$tparamy = {
    val len = self.length
    val array = new Array[$y](len)
    var i = 0
    while(i < len){
      array(len - i - 1) = f(self(i))
      i += 1
    }
    new of$tparamy(array)
  }
"""
    }

    val flatMap: Type => String = { b =>
      import b.*
      s"""
  def flatMap$yWithTag(f: $a => Array[$y]): of$tparamy = {
    val builder = new ArrayBuilder.of$tparamy()
    var i = 0
    while(i < self.length){
      val x = f(self(i))
      var j = 0
      while(j < x.length){
        builder += x(j)
        j += 1
      }
      i += 1
    }
    new of$tparamy(builder.result())
  }
"""
    }

    val collect: Type => String = { b =>
      import b.*
      s"""
  $collectIsSlow
  def collect$yWithTag(f: PartialFunction[$a, $y]): of$tparamy = {
    val builder = new ArrayBuilder.of$tparamy()
    var i = 0
    val appendFunc = f.runWith(builder += _)
    while(i < self.length){
      appendFunc(self(i))
      i += 1
    }
    new of$tparamy(builder.result())
  }
"""
    }

    val collectFirst: Type => String = { b =>
      import b.*
      s"""
  def collectFirst$yWithTag(f: PartialFunction[$a, $y]): Option[$y] = {
    var i = 0
    while(i < self.length){
      if(f isDefinedAt self(i)){
        return Some(f(self(i)))
      }
      i += 1
    }
    None
  }
"""
    }

    // could not use @specialized annotation with value class
    // https://gist.github.com/xuwei-k/7153650
    val foldLeft: Type => String = { b =>
      import b.*
      s"""
  def foldLeft$tparamy(z: $y)(f: ($y, $a) => $y): $y = {
    var i = 0
    var acc = z
    while(i < self.length){
      acc = f(acc, self(i))
      i += 1
    }
    acc
  }
"""
    }

    val foldRight: Type => String = { b =>
      import b.*
      s"""
  def foldRight$tparamy(z: $y)(f: ($a, $y) => $y): $y = {
    var i = self.length - 1
    var acc = z
    while(i >= 0){
      acc = f(self(i), acc)
      i -= 1
    }
    acc
  }
"""
    }

    val scanLeft: Type => String = { b =>
      import b.*
      s"""
  def scanLeft$yWithTag(z: $y)(f: ($y, $a) => $y): of$tparamy = {
    val array = new Array[$y](self.length + 1)
    array(0) = z
    var i = 0
    while(i < self.length){
      array(i + 1) = f(array(i), self(i))
      i += 1
    }
    new of$tparamy(array)
  }
"""
    }

    val scanRight: Type => String = { b =>
      import b.*
      s"""
  def scanRight$yWithTag(z: $y)(f: ($a, $y) => $y): of$tparamy = {
    val array = new Array[$y](self.length + 1)
    array(self.length) = z
    var i = self.length
    while(i > 0){
      array(i - 1) = f(self(i - 1), array(i))
      i -= 1
    }
    new of$tparamy(array)
  }
"""
    }

    val foldMapLeft1: Type => String = { b =>
      import b.*
      s"""
  def foldMapLeft1$tparamy(z: $a => $y)(f: ($y, $a) => $y): Option[$y] = {
    if(self.length == 0){
      None
    }else{
      var acc = z(self(0))
      var i = 1
      while(i < self.length){
        acc = f(acc, self(i))
        i += 1
      }
      Some(acc)
    }
  }
"""
    }

    val foldMapRight1: Type => String = { b =>
      import b.*
      s"""
  def foldMapRight1$tparamy(z: $a => $y)(f: ($a, $y) => $y): Option[$y] = {
    if(self.length == 0){
      None
    }else{
      var acc = z(self(self.length - 1))
      var i = self.length - 2
      while(i >= 0){
        acc = f(self(i), acc)
        i -= 1
      }
      Some(acc)
    }
  }
"""
    }

    (List[Type => String](
      map,
      reverseMap,
      flatMap,
      collect,
      collectFirst,
      foldLeft,
      foldRight,
      scanLeft,
      scanRight,
      foldMapLeft1,
      foldMapRight1
    ).map { method =>
      withRef map method mkString "\n"
    } ::: List[String](
      map0,
      reverseMap0,
      flatMap0,
      collect0,
      collectFirst0,
      foldLeft0,
      foldRight0,
      scanLeft0,
      scanRight0,
      foldMapLeft10,
      foldMapRight10
    )).mkString("\n\n")

  }
}
