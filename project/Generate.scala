package nobox

import sbt._
import nobox.Type._

object Generate{

  val list = List(BOOL, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE)

  def apply(dir: File): Seq[File] = {
    list.map{ t =>
      val f = dir / ("of" + t + ".scala")
      IO.write(f, src(t))
      f
    }
  }

  def src(a: Type): String = {

    val clazz = "of" + a

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
      case BOOL => ""
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
      case BOOL => ""
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
      case BOOL => ""
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

    val methods: String = List(
      map, reverseMap, flatMap, collect, collectFirst, foldLeft, foldRight, scanLeft, scanRight
    ).map{ method =>
      list.map(_.toString) map method mkString "\n"
    }.mkString("\n\n")

s"""package nobox

import java.util.Arrays
import scala.reflect.ClassTag
import scala.collection.mutable.ArrayBuilder

final class $clazz (val self: Array[$a]) extends AnyVal {
  $methods

  $sum

  $sumLong

  $product

  $productLong

  $productDouble

  $sorted

  $map0

  $flatMap0

  $reverseMap0

  $collect0

  $foldLeft0

  $foldRight0

  def foreach[U](f: $a => U): Unit = {
    var i = 0
    while(i < self.length){
      f(self(i))
      i += 1
    }
  }

  def filter(f: $a => Boolean): $clazz = {
    val builder = new ArrayBuilder.of$a()
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        builder += self(i)
      }
      i += 1
    }
    new $clazz(builder.result)
  }

  def filterNot(f: $a => Boolean): $clazz = filter(!f(_))

  def withFilter(f: $a => Boolean): $clazz = filter(f)

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

  def take(n: Int): $clazz = {
    if(n >= self.length){
      this
    }else if(n <= 0){
      $clazz.empty
    }else{
      new $clazz(Arrays.copyOf(self, n))
    }
  }

  def takeWhile(f: $a => Boolean): $clazz = {
    val len = index(!f(_))
    if(len < 0){
      this
    }else if(len == 0){
      $clazz.empty
    }else{
      new $clazz(Arrays.copyOf(self, len))
    }
  }

  def takeRight(n: Int): $clazz = {
    if(n <= 0){
      $clazz.empty
    }else if(n >= self.length){
      this
    }else{
      val start = self.length - n
      new $clazz(Arrays.copyOfRange(self, start, self.length))
    }
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

  def reverse_:::(prefix: $clazz): $clazz = {
    if(prefix.length == 0){
      this
    }else{
      val array = new Array[$a](self.length + prefix.length)
      var i = 0
      val len = prefix.length
      while(i < len){
        array(i) = prefix.self(len - i - 1)
        i += 1
      }
      System.arraycopy(self, 0, array, prefix.length, self.length)
      new $clazz(array)
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

  def drop(n: Int): $clazz = {
    if(n <= 0){
      this
    }else if(n >= self.length){
      $clazz.empty
    }else{
      new $clazz(Arrays.copyOfRange(self, n, self.length))
    }
  }

  def dropWhile(f: $a => Boolean): $clazz = {
    val len = index(!f(_))
    if(len < 0){
      $clazz.empty
    }else if(len == 0){
      this
    }else{
      new $clazz(Arrays.copyOfRange(self, len, self.length))
    }
  }

  def dropRight(n: Int): $clazz = {
    if(n <= 0){
      this
    }else if(n >= self.length){
      $clazz.empty
    }else{
      new $clazz(Arrays.copyOf(self, self.length - n))
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

  def splitAt(n: Int): ($clazz, $clazz) = {
    if(n <= 0){
      ($clazz.empty, this)
    }else if(n >= self.length){
      (this, $clazz.empty)
    }else{
      (new $clazz(Arrays.copyOf(self, n)), new $clazz(Arrays.copyOfRange(self, n, self.length)))
    }
  }

  def span(f: $a => Boolean): ($clazz, $clazz) = {
    val n = index(!f(_))
    if(n < 0){
      (this, $clazz.empty)
    }else if(n >= self.length){
      ($clazz.empty, this)
    }else{
      (new $clazz(Arrays.copyOf(self, n)), new $clazz(Arrays.copyOfRange(self, n, self.length)))
    }
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

  def partition(f: $a => Boolean): ($clazz, $clazz) = {
    val l, r = new ArrayBuilder.of$a()
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        l += self(i)
      }else{
        r += self(i)
      }
      i += 1
    }
    (new $clazz(l.result), new $clazz(r.result))
  }

  @throws[IndexOutOfBoundsException]
  def updated(index: Int, elem: $a): $clazz = {
    val array = self.clone
    array(index) = elem
    new $clazz(array)
  }

  def slice(from: Int, until: Int): $clazz = {
    if(until <= from || until <= 0 || from >= self.length){
      $clazz.empty
    }else if(from <= 0 && self.length <= until){
      this
    }else{
      new $clazz(Arrays.copyOfRange(self, from max 0, until min self.length))
    }
  }

  def reduceLeftOption(f: ($a, $a) => $a): Option[$a] = {
    if(self.length == 0) return None

    var i = 1
    var acc = self(0)
    while(i < self.length){
      acc = f(acc, self(i))
      i += 1
    }
    Some(acc)
  }

  def reduceRightOption(f: ($a, $a) => $a): Option[$a] = {
    if(self.length == 0) return None

    var i = self.length - 2
    var acc = self(self.length - 1)
    while(i >= 0){
      acc = f(self(i), acc)
      i -= 1
    }
    Some(acc)
  }

  def foldLeftAny[A](z: A)(f: (A, $a) => A): A = {
    var i = 0
    var acc = z
    while(i < self.length){
      acc = f(acc, self(i))
      i += 1
    }
    acc
  }

  def foldRightAny[A](z: A)(f: ($a, A) => A): A = {
    var i = self.length - 1
    var acc = z
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

  def tailOption: Option[$clazz] = {
    if(self.length != 0){
      Some(drop(1))
    }else{
      None
    }
  }

  def tails: Iterator[$clazz] = new Iterator[$clazz]{
    private[this] var i = 0
    var hasNext = true
    def next: $clazz = {
      val r = new $clazz(Arrays.copyOfRange(self, i, self.length))
      i += 1
      if(i > self.length) hasNext = false
      r
    }
  }

  def inits: Iterator[$clazz] = new Iterator[$clazz]{
    private[this] var i = self.length
    var hasNext = true
    def next: $clazz = {
      val r = new $clazz(Arrays.copyOfRange(self, 0, i))
      i -= 1
      if(i < 0) hasNext = false
      r
    }
  }

  def initOption: Option[$clazz] = {
    if(self.length != 0){
      Some(dropRight(1))
    }else{
      None
    }
  }

  def length: Int = self.length

  def size: Int = self.length

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

  override def toString = mkString("$clazz(", ", ", ")")

  def mkString(start: String, sep: String, end: String): String =
    addString(new StringBuilder(), start, sep, end).toString

  def mkString(sep: String): String = mkString("", sep, "")

  def mkString: String = mkString("")

  def addString(b: StringBuilder, start: String, sep: String, end: String): StringBuilder = {
    var first = true

    b append start
    var i = 0
    while(i < self.length){
      if (first) {
        b append self(i)
        first = false
      }
      else {
        b append sep
        b append self(i)
      }
      i += 1
    }
    b append end

    b
  }

  def ===(that: $clazz): Boolean = Arrays.equals(self, that.self)

  @throws[IllegalArgumentException]
  def grouped(n: Int): Iterator[$clazz] = sliding(n, n)

  @throws[IllegalArgumentException]
  def sliding(_size: Int, step: Int = 1): Iterator[$clazz] = {
    require(_size > 0, "size must be positive number")
    require(step > 0, "step must be positive number")
    new Iterator[$clazz]{
      private[this] var i = 0
      var hasNext = self.length != 0
      def next = {
        // n is negative, if `i + _size` overflow
        val n = i + _size
        val until = if(n > 0) math.min(n, self.length) else self.length
        val r = new $clazz(Arrays.copyOfRange(self, i, until))
        i += step
        if(i >= self.length || n > self.length || n < 0) hasNext = false
        r
      }
    }
  }

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

  def scanLeft[A: reflect.ClassTag](z: A)(f: (A, $a) => A): Array[A] = {
    val array = new Array[A](self.length + 1)
    array(0) = z
    var i = 0
    while(i < self.length){
      array(i + 1) = f(array(i), self(i))
      i += 1
    }
    array
  }

  def scanRight[A: reflect.ClassTag](z: A)(f: ($a, A) => A): Array[A] = {
    val array = new Array[A](self.length + 1)
    array(self.length) = z
    var i = self.length
    while(i > 0){
      array(i - 1) = f(self(i - 1), array(i))
      i -= 1
    }
    array
  }

  def scanLeft1(f: ($a, $a) => $a): $clazz = {
    if(self.length != 0){
      val array = new Array[$a](self.length)
      array(0) = self(0)
      var i = 0
      while(i < self.length - 1){
        array(i + 1) = f(array(i), self(i + 1))
        i += 1
      }
      new $clazz(array)
    }else{
      $clazz.empty
    }
  }

  def scanRight1(f: ($a, $a) => $a): $clazz = {
    if(self.length != 0){
      val array = new Array[$a](self.length)
      array(self.length - 1) = self(self.length - 1)
      var i = self.length - 1
      while(i > 0){
        array(i - 1) = f(self(i - 1), array(i))
        i -= 1
      }
      new $clazz(array)
    }else{
      $clazz.empty
    }
  }

  def startsWith(that: Array[$a], offset: Int = 0): Boolean = {
    require(offset >= 0, "offset = " + offset  + " is invalid. offset must be positive")
    var i = offset
    var j = 0
    val thisLen = self.length
    val thatLen = that.length
    while (i < thisLen && j < thatLen && self(i) == that(j)) {
      i += 1
      j += 1
    }
    j == thatLen
  }

  def endsWith(that: Array[$a]): Boolean = {
    var i = length - 1
    var j = that.length - 1

    (j <= i) && {
      while (j >= 0){
        if(self(i) != that(j)){
          return false
        }
        i -= 1
        j -= 1
      }
      true
    }
  }

  def groupBy[A](f: $a => A): Map[A, $clazz] = {
    val m = collection.mutable.Map.empty[A, ArrayBuilder.of$a]
    var i = 0
    while(i < self.length){
      val key = f(self(i))
      m.getOrElseUpdate(key, new ArrayBuilder.of$a) += self(i)
      i += 1
    }

    val b = Map.newBuilder[A, $clazz]
    m.foreach{ case (k, v) =>
      b += ((k, new $clazz(v.result)))
    }

    b.result
  }
}

object $clazz {

  def apply(elems: $a *): $clazz = new $clazz(elems.toArray)

  val empty: $clazz = new $clazz(new Array[$a](0))

  def iterate(start: $a, len: Int)(f: $a => $a): of$a = {
    if(len == 0){
      empty
    }else{
      val array = new Array[$a](len)
      var i = 1
      array(0) = start
      while (i < len) {
        array(i) = f(array(i - 1))
        i += 1
      }
      new $clazz(array)
    }
  }

  def tabulate(n: Int)(f: Int => $a): $clazz = {
    val array = new Array[$a](n)
    var i = 0
    while (i < n) {
      array(i) = f(i)
      i += 1
    }
    new $clazz(array)
  }

  def flatten(xs: Array[Array[$a]]): $clazz = {
    var i = 0
    var n = 0
    val length = xs.length
    while(i < length){
      n += xs(i).length
      i += 1
    }
    val array = new Array[$a](n)
    i = 0
    n = 0
    while(i < length){
      val elem = xs(i)
      System.arraycopy(elem, 0, array, n, elem.length)
      n += elem.length
      i += 1
    }
    new $clazz(array)
  }

}
"""
  }
}

