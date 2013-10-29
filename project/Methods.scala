package nobox

import nobox.Type._
import Generate.list

object Methods{

  def apply(a: Type): String = {

    val clazz = "of" + a.name + a.tparamx

    val map0: String = {

      val cases: String = list.map{ b =>
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

      val cases: String = list.map{ b =>
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

      val cases: String = list.map{ b =>
        s"      case ClassTag.$b => reverseMap$b(f.asInstanceOf[$a => $b]).self"
      }.mkString("\n")

s"""
  def reverseMap[A](f: $a => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.reverseMap(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val collect0: String = {
      val cases: String = list.map{ b =>
        s"      case ClassTag.$b => collect$b(f.asInstanceOf[PartialFunction[$a, $b]]).self"
      }.mkString("\n")

s"""
  def collect[A](f: PartialFunction[$a, A])(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.collect(f)
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val foldLeft0: String = {
      val cases: String = list.map{ b =>
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
      val cases: String = list.map{ b =>
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

    val map: String => String = { b =>
s"""
  def map$b(f: $a => $b): of$b = {
    val array = new Array[$b](self.length)
    var i = 0
    while(i < self.length){
      array(i) = f(self(i))
      i += 1
    }
    new of$b(array)
  }
"""
    }

    val reverseMap: String => String = { b =>
s"""
  def reverseMap$b(f: $a => $b): of$b = {
    val len = self.length
    val array = new Array[$b](len)
    var i = 0
    while(i < len){
      array(len - i - 1) = f(self(i))
      i += 1
    }
    new of$b(array)
  }
"""
    }

    val flatMap: String => String = { b =>
s"""
  def flatMap$b(f: $a => Array[$b]): of$b = {
    val builder = new ArrayBuilder.of$b()
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
    new of$b(builder.result)
  }
"""
    }

    val collect: String => String = { b =>
s"""
  def collect$b(f: PartialFunction[$a, $b]): of$b = {
    val builder = new ArrayBuilder.of$b()
    var i = 0
    while(i < self.length){
      if(f isDefinedAt self(i)){
        builder += f(self(i))
      }
      i += 1
    }
    new of$b(builder.result)
  }
"""
    }

    val collectFirst: String => String = { b =>
s"""
  def collectFirst$b(f: PartialFunction[$a, $b]): Option[$b] = {
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
    val foldLeft: String => String = { b =>
s"""
  def foldLeft$b(z: $b)(f: ($b, $a) => $b): $b = {
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

    val foldRight: String => String = { b =>
s"""
  def foldRight$b(z: $b)(f: ($a, $b) => $b): $b = {
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

    val sum: String = a match {
      case BYTE | CHAR | SHORT | INT =>
s"""
  def sum: Int = {
    var i, n = 0
    while(i < self.length){
      n += self(i)
      i += 1
    }
    n
  }
"""
      case BOOL | REF => "" // TODO Ref
      case DOUBLE | FLOAT | LONG =>
s"""
  def sum: $a = {
    var i = 0
    var n: $a = 0
    while(i < self.length){
      n += self(i)
      i += 1
    }
    n
  }
"""
    }

    val sumLong: String = a match {
      case INT =>
s"""
  def sumLong: Long = {
    var i = 0
    var n: Long = 0L
    while(i < self.length){
      n += self(i)
      i += 1
    }
    n
  }
"""
      case _ => ""
    }

    val product: String = a match {
      case BYTE | CHAR | SHORT | INT =>
s"""
  def product: Int = {
    var i = 0
    var n = 1
    while(i < self.length){
      n *= self(i)
      i += 1
    }
    n
  }
"""
      case BOOL | REF => "" // TODO Ref
      case DOUBLE | FLOAT | LONG =>
s"""
  def product: $a = {
    var i = 0
    var n: $a = 1
    while(i < self.length){
      n *= self(i)
      i += 1
    }
    n
  }
"""
    }

    val productLong: String = a match {
      case BYTE | CHAR | SHORT | INT =>
s"""
  def productLong: Long = {
    var i = 0
    var n: Long = 1L
    while(i < self.length){
      n *= self(i)
      i += 1
    }
    n
  }
"""
      case _ => ""
    }

    val productDouble: String = a match {
      case BYTE | CHAR | SHORT | INT | LONG | FLOAT =>
s"""
  def productDouble: Double = {
    var i = 0
    var n: Double = 1.0
    while(i < self.length){
      n *= self(i)
      i += 1
    }
    n
  }
"""
      case _ => ""
    }

    val sorted: String = a match {
      case BOOL | REF => "" // TODO Ref
      case _ =>
s"""
  def sorted: $clazz = {
    val array = self.clone
    Arrays.sort(array)
    new $clazz(array)
  }
"""
    }

    val scanLeft: String => String = { b =>
s"""
  def scanLeft$b(z: $b)(f: ($b, $a) => $b): of$b = {
    val array = new Array[$b](self.length + 1)
    array(0) = z
    var i = 0
    while(i < self.length){
      array(i + 1) = f(array(i), self(i))
      i += 1
    }
    new of$b(array)
  }
"""
}

    val scanRight: String => String = { b =>
s"""
  def scanRight$b(z: $b)(f: ($a, $b) => $b): of$b = {
    val array = new Array[$b](self.length + 1)
    array(self.length) = z
    var i = self.length
    while(i > 0){
      array(i - 1) = f(self(i - 1), array(i))
      i -= 1
    }
    new of$b(array)
  }
"""
}

    val maxAndMin: String = {
      if(a == REF) ""
      else {
s"""
  def max: Option[$a] = {
    if(self.length == 0){
      None
    }else{
      var i = 1
      var n = self(0)
      while(i < self.length){
        val x = self(i)
        if(n < x){
          n = x
        }
        i += 1
      }
      Some(n)
    }
  }

  def min: Option[$a] = {
    if(self.length == 0){
      None
    }else{
      var i = 1
      var n = self(0)
      while(i < self.length){
        val x = self(i)
        if(n > x){
          n = x
        }
        i += 1
      }
      Some(n)
    }
  }
"""
      }
    }

    (List(
      map, reverseMap, flatMap, collect, collectFirst, foldLeft, foldRight, scanLeft, scanRight
    ).map{ method =>
      list.map(_.toString) map method mkString "\n"
    } ::: List(
      sum, sumLong, product, productLong, productDouble, sorted, map0, flatMap0,
      reverseMap0, collect0, foldLeft0, foldRight0, maxAndMin
    )).mkString("\n\n")

  }
}

