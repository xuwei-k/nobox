package nobox

object One {

  def src(a: Type): String = {
    import Type.REF

    val clazz = "of" + a.name + a.tparamx + "1"
    val zero = "of" + a.name
    val withTag = a.xWithTag
    val obj = "of" + a.name + "1"
    val empty = zero + "." + "empty" + a.tparamx
    val cast1 = if (a == REF) s".asInstanceOf[Array[$a & AnyRef]]" else ""
    val cast2 = if (a == REF) s".asInstanceOf[Array[$a]]" else ""
    val castObj = if (a == REF) s".asInstanceOf[Array[AnyRef]]" else ""
    def copyOf(n: String) = {
      s"Arrays.copyOf${a.tparamx}(self$cast1, $n)$cast2"
    }

    def copyOfRange(start: String, end: String) = {
      s"Arrays.copyOfRange${a.tparamx}(self$cast1, $start, $end)$cast2"
    }

    s"""
package nobox

import scala.collection.mutable.ArrayBuilder
import java.util.Arrays

final class ${clazz} private(val self: Array[${a}]) extends AnyVal {

  def head: $a = self(0)

  def tail: $zero = {
    new $zero(${copyOfRange("1", "self.length")})
  }

  def length: Int = self.length

  def foreach[U](f: $a => U): Unit = {
    var i = 0
    while(i < self.length){
      f(self(i))
      i += 1
    }
  }

  def filter(f: $a => Boolean): $zero = {
    val builder = new ArrayBuilder.$zero()
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        builder += self(i)
      }
      i += 1
    }
    new $zero(builder.result())
  }

  def filterNot(f: $a => Boolean): $zero = filter(!f(_))

  def find(f: $a => Boolean): Option[$a] = {
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        return Some(self(i))
      }
      i += 1
    }
    None
  }

  def exists(f: $a => Boolean): Boolean = {
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        return true
      }
      i += 1
    }
    false
  }

  def forall(f: $a => Boolean): Boolean = !exists(!f(_))

  def toList: List[${a}] = {
    var list = self(self.length - 1) :: Nil
    var i = self.length - 2
    while(i >= 0){
      list ::= self(i)
      i -= 1
    }
    list
  }

  def toArray: Array[$a] = self.clone

  def take(n: Int): $zero = {
    if(n >= self.length){
      new $zero(self)
    }else if(n <= 0){
      $empty
    }else{
      new $zero(${copyOf("n")})
    }
  }

  def takeWhile(f: $a => Boolean): $zero = {
    val len = index(!f(_))
    if(len < 0){
      new $zero(self)
    }else if(len == 0){
      $empty
    }else{
      new $zero(${copyOf("len")})
    }
  }

  def count(f: $a => Boolean): Int = {
    var i = 0
    var n = 0
    while(i < self.length){
      if(f(self(i))){
        n += 1
      }
      i += 1
    }
    n
  }

  @inline private def index(f: $a => Boolean): Int = {
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        return i
      }
      i += 1
    }
    -1
  }

  def reverse: $clazz = {
    var i = 0
    val len = self.length
    val array = new Array[$a](len)
    while(i < len){
      array(len - i - 1) = self(i)
      i += 1
    }
    new $clazz(array)
  }

  def drop(n: Int): $zero = {
    if(n <= 0){
      new $zero(self)
    }else if(n >= self.length){
      $empty
    }else{
      new $zero(${copyOfRange("n", "self.length")})
    }
  }

  def dropWhile(f: $a => Boolean): $zero = {
    val len = index(!f(_))
    if(len < 0){
      $empty
    }else if(len == 0){
      new $zero(self)
    }else{
      new $zero(${copyOfRange("len", "self.length")})
    }
  }

  def dropRight(n: Int): $zero = {
    if(n <= 0){
      new $zero(self)
    }else if(n >= self.length){
      $empty
    }else{
      new $zero(${copyOf("self.length - n")})
    }
  }

  def contains(elem: $a): Boolean = {
    var i = 0
    while(i < self.length){
      if(self(i) == elem){
        return true
      }
      i += 1
    }
    false
  }

  def ++(that: $clazz): $clazz = {
    if(self.length == 0){
      that
    }else if(that.length == 0){
      this
    }else{
      val size1 = self.length
      val size2 = that.length
      val array = new Array[$a](size1 + size2)
      System.arraycopy(self, 0, array, 0, size1)
      System.arraycopy(that.self, 0, array, size1, size2)
      new $clazz(array)
    }
  }

  def reduceLeft(f: ($a, $a) => $a): $a = {
    var i = 1
    var acc = self(0)
    while(i < self.length){
      acc = f(acc, self(i))
      i += 1
    }
    acc
  }

  def reduceRight(f: ($a, $a) => $a): $a = {
    var i = self.length - 2
    var acc = self(self.length - 1)
    while(i >= 0){
      acc = f(self(i), acc)
      i -= 1
    }
    acc
  }

  def indexOf(elem: $a): Option[Int] = {
    var i = 0
    while(i < self.length){
      if(self(i) == elem){
        return Some(i)
      }
      i += 1
    }
    None
  }

  def lastIndexOf(elem: $a): Option[Int] = {
    var i = self.length - 1
    while(i >= 0){
      if(self(i) == elem){
        return Some(i)
      }
      i -= 1
    }
    None
  }

  def init: $zero = dropRight(1)

  override def toString = mkString("$obj(", ", ", ")")

  def mkString(start: String, sep: String, end: String): String =
    addString(new StringBuilder(), start, sep, end).toString

  def mkString(sep: String): String = mkString("", sep, "")

  def mkString: String = mkString("")

  def addString(b: StringBuilder, start: String, sep: String, end: String): StringBuilder = {
    b append start
    b append self(0)
    var i = 1
    while(i < self.length){
      b append sep
      b append self(i)
      i += 1
    }
    b append end
    b
  }

  @inline
  def ===(that: $clazz): Boolean = Arrays.equals(self$castObj, that.self$castObj)


  def scanLeft1(f: ($a, $a) => $a): $clazz = {
    val array = new Array[$a](self.length)
    array(0) = self(0)
    var i = 0
    while(i < self.length - 1){
      array(i + 1) = f(array(i), self(i + 1))
      i += 1
    }
    new $clazz(array)
  }

  def scanRight1(f: ($a, $a) => $a): $clazz = {
    val array = new Array[$a](self.length)
    array(self.length - 1) = self(self.length - 1)
    var i = self.length - 1
    while(i > 0){
      array(i - 1) = f(self(i - 1), array(i))
      i -= 1
    }
    new $clazz(array)
  }

  def maxBy[A](f: $a => A)(implicit A: Ordering[A]): $a = {
    var maxF = f(self(0))
    var maxElem = self(0)
    var i = 1
    while(i < self.length){
      val fx = f(self(i))
      if (A.gt(fx, maxF)) {
        maxElem = self(i)
        maxF = fx
      }
      i += 1
    }
    maxElem
  }

  def minBy[A](f: $a => A)(implicit A: Ordering[A]): $a = {
    var minF = f(self(0))
    var minElem = self(0)
    var i = 1
    while(i < self.length){
      val fx = f(self(i))
      if (A.lt(fx, minF)) {
        minElem = self(i)
        minF = fx
      }
      i += 1
    }
    minElem
  }

  def max: $a = {
    var i = 1
    var n = self(0)
    while(i < self.length){
      val x = self(i)
      if(n < x){
        n = x
      }
      i += 1
    }
    n
  }

  def min: $a = {
    var i = 1
    var n = self(0)
    while(i < self.length){
      val x = self(i)
      if(n > x){
        n = x
      }
      i += 1
    }
    n
  }

  def minmax: ($a, $a) = {
    var i = 1
    var _min, _max = self(0)
    while(i < self.length){
      val x = self(i)
      if(_min > x){
        _min = x
      }else if(_max < x){
        _max = x
      }
      i += 1
    }
    (_min, _max)
  }

  ${a match {
        case Type.BOOL | Type.REF => ""
        case _ =>
          s"""
  def sorted: $clazz = {
    val array = self.clone
    Arrays.sort(array)
    new $clazz(array)
  }
"""
      }}
}

object $obj {

  def apply${withTag}(elem0: $a, elems: $a *): $clazz = {
    val builder = new ArrayBuilder.$zero()
    builder += elem0
    builder ++= elems
    new $clazz(builder.result())
  }

}

"""
  }

}
