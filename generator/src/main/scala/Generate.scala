package nobox

import nobox.Type._
import java.io.File
import java.nio.file.Files
import java.nio.charset.Charset

object Generate{

  private def deleteDir(file: File): Unit = {
    if(!file.delete() && file.isDirectory){
      Option(file.listFiles).toList.flatten.foreach(deleteDir)
      file.delete
    }
  }

  def main(args: Array[String]): Unit = {
    val dir = new File(args.headOption.getOrElse(sys.error("invalid args " + args.mkString(", "))))
    deleteDir(dir)
    apply(dir)
  }

  val list = List(INT, LONG, FLOAT, DOUBLE, BYTE, CHAR, SHORT, BOOL)
  val withRef = list :+ REF

  def apply(dir: File): Unit = {
    dir.mkdir
    withRef.foreach{ t =>
      val f = new File(dir, "of" + t.name + ".scala").toPath
      Files.write(f, java.util.Collections.singletonList(src(t)), Charset.forName("UTF-8"))
    }
    list.foreach{ t =>
      val f = new File(dir, "of" + t.name + "1.scala").toPath
      Files.write(f, java.util.Collections.singletonList(One.src(t)), Charset.forName("UTF-8"))
    }
  }

  def src(a: Type): String = {

    val clazz = "of" + a.name + a.tparamx
    val withTag = a.xWithTag
    val classWithTag = "of" + a.name + withTag
    val obj = "of" + a.name
    val parent = if(a == REF) "AnyRef" else "AnyVal"
    val empty = obj + "." + "empty" + a.tparamx
    val cast1 = if(a == REF) s".asInstanceOf[Array[$a with AnyRef]]" else ""
    val cast2 = if(a == REF) s".asInstanceOf[Array[$a]]" else ""
    val castObj = if(a == REF) s".asInstanceOf[Array[AnyRef]]" else ""
    def copyOf(n: String) = {
      s"Arrays.copyOf${a.tparamx}(self$cast1, $n )$cast2"
    }

    def copyOfRange(start: String, end: String) = {
      s"Arrays.copyOfRange${a.tparamx}(self$cast1, $start, $end )$cast2"
    }


s"""package nobox

import java.util.Arrays
import scala.reflect.ClassTag
import scala.collection.mutable.ArrayBuilder

final class WithFilter${a.name + withTag} private[nobox](self: $clazz, f: $a => Boolean){
  ${WithFilter(a)}
}

final class $classWithTag (val self: Array[$a]) extends $parent {

  ${Methods(a)}
  ${SpecializedMethods(a)}

  def foreach[U](f: $a => U): Unit = {
    var i = 0
    while(i < self.length){
      f(self(i))
      i += 1
    }
  }

  def filter(f: $a => Boolean): $clazz = {
    val builder = new ArrayBuilder.$clazz()
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        builder += self(i)
      }
      i += 1
    }
    new $clazz(builder.result())
  }

  def filterNot(f: $a => Boolean): $clazz = filter(!f(_))

  def withFilter(f: $a => Boolean): WithFilter${a.name + a.tparamx} =
    new WithFilter${a.name + a.tparamx}(this, f)

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
      $empty
    }else{
      new $clazz(${copyOf("n")})
    }
  }

  def takeWhile(f: $a => Boolean): $clazz = {
    val len = index(!f(_))
    if(len < 0){
      this
    }else if(len == 0){
      $empty
    }else{
      new $clazz(${copyOf("len")})
    }
  }

  def takeWhileR(f: $a => Boolean): $clazz = {
    val len = lastIndex(f) + 1
    if(len <= 0){
      this
    }else if(len == self.length){
      $empty
    }else{
      new $clazz(${copyOfRange("len", "self.length")})
    }
  }

  def takeRight(n: Int): $clazz = {
    if(n <= 0){
      $empty
    }else if(n >= self.length){
      this
    }else{
      val start = self.length - n
      new $clazz(${copyOfRange("start", "self.length")})
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
      $empty
    }else{
      new $clazz(${copyOfRange("n", "self.length")})
    }
  }

  def dropWhile(f: $a => Boolean): $clazz = {
    val len = index(!f(_))
    if(len < 0){
      $empty
    }else if(len == 0){
      this
    }else{
      new $clazz(${copyOfRange("len", "self.length")})
    }
  }

  def dropWhileR(f: $a => Boolean): $clazz = {
    val len = lastIndex(f) + 1
    if(len <= 0){
      $empty
    }else if(len == self.length){
      this
    }else{
      new $clazz(${copyOf("len")})
    }
  }

  def dropRight(n: Int): $clazz = {
    if(n <= 0){
      this
    }else if(n >= self.length){
      $empty
    }else{
      new $clazz(${copyOf("self.length - n")})
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
      ($empty, this)
    }else if(n >= self.length){
      (this, $empty)
    }else{
      (new $clazz(${copyOf("n")}), new $clazz(${copyOfRange("n", "self.length")}))
    }
  }

  def span(f: $a => Boolean): ($clazz, $clazz) = {
    val n = index(!f(_))
    if(n < 0){
      (this, $empty)
    }else if(n >= self.length){
      ($empty, this)
    }else{
      (new $clazz(${copyOf("n")}), new $clazz(${copyOfRange("n", "self.length")}))
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
    val l, r = new ArrayBuilder.$clazz()
    var i = 0
    while(i < self.length){
      if(f(self(i))){
        l += self(i)
      }else{
        r += self(i)
      }
      i += 1
    }
    (new $clazz(l.result()), new $clazz(r.result()))
  }

  @throws[IndexOutOfBoundsException]
  def updated(index: Int, elem: $a): $clazz = {
    val array = self.clone
    array(index) = elem
    new $clazz(array)
  }

  def slice(from: Int, until: Int): $clazz = {
    if(until <= from || until <= 0 || from >= self.length){
      $empty
    }else if(from <= 0 && self.length <= until){
      this
    }else{
      new $clazz(${copyOfRange("from max 0", "until min self.length")})
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
    def next(): $clazz = {
      val r = new $clazz(${copyOfRange("i", "self.length")})
      i += 1
      if(i > self.length) hasNext = false
      r
    }
  }

  def inits: Iterator[$clazz] = new Iterator[$clazz]{
    private[this] var i = self.length
    var hasNext = true
    def next(): $clazz = {
      val r = new $clazz(${copyOfRange("0", "i")})
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

  @inline def length: Int = self.length

  @inline def size: Int = self.length

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

  @inline private def lastIndex(f: $a => Boolean): Int = {
    var i = self.length - 1
    while(0 <= i){
      if(!f(self(i))){
        return i
      }
      i -= 1
    }
    -1
  }

  def toArray: Array[$a] = self.clone

  def toList: List[${a}] = {
    var list: List[$a] = Nil
    var i = self.length - 1
    while(i >= 0){
      list ::= self(i)
      i -= 1
    }
    list
  }

  override def toString = mkString("$obj(", ", ", ")")

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

  @inline
  def ===(that: $clazz): Boolean = Arrays.equals(self$castObj, that.self$castObj)

  @throws[IllegalArgumentException]
  def grouped(n: Int): Iterator[$clazz] = sliding(n, n)

  @throws[IllegalArgumentException]
  def sliding(_size: Int, step: Int = 1): Iterator[$clazz] = {
    require(_size > 0, "size must be positive number")
    require(step > 0, "step must be positive number")
    new Iterator[$clazz]{
      private[this] var i = 0
      var hasNext = self.length != 0
      def next() = {
        // n is negative, if `i + _size` overflow
        val n = i + _size
        val until = if(n > 0) math.min(n, self.length) else self.length
        val r = new $clazz(${copyOfRange("i", "until")})
        i += step
        if(i >= self.length || n > self.length || n < 0) hasNext = false
        r
      }
    }
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
      $empty
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
      $empty
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
    val m = collection.mutable.Map.empty[A, ArrayBuilder.${clazz}]
    var i = 0
    while(i < self.length){
      val key = f(self(i))
      m.getOrElseUpdate(key, new ArrayBuilder.${clazz}) += self(i)
      i += 1
    }

    val b = Map.newBuilder[A, $clazz]
    m.foreach{ case (k, v) =>
      b += ((k, new $clazz(v.result())))
    }

    b.result()
  }

  def maxBy[A](f: $a => A)(implicit A: Ordering[A]): Option[$a] = {
    if(self.length == 0){
      None
    }else{
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
      Some(maxElem)
    }
  }

  def minBy[A](f: $a => A)(implicit A: Ordering[A]): Option[$a] = {
    if(self.length == 0){
      None
    }else{
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
      Some(minElem)
    }
  }

  def deleteFirst(elem: $a): $clazz = {
    var i = 0
    while(i < self.length){
      if(self(i) == elem){
        val array = new Array[$a](self.length - 1)
        System.arraycopy(self, 0, array, 0, i)
        System.arraycopy(self, i + 1, array, i, self.length - i - 1)
        return new $clazz(array)
      }
      i += 1
    }
    this
  }

  def interleave(that: $clazz): $clazz = {
    if(self.length == 0){
      that
    }else if(that.self.length == 0){
      this
    }else{
      val len = Math.min(self.length, that.self.length)
      val array = new Array[$a](self.length + that.self.length)
      @annotation.tailrec
      def loop(isThis: Boolean, i: Int): Unit = {
        if(i < len) {
          if (isThis) {
            array(i * 2) = self(i)
            loop(false, i)
          } else {
            array(i * 2 + 1) = that.self(i)
            loop(true, i + 1)
          }
        }
      }
      loop(true, 0)
      def cp(min: Array[$a], max: Array[$a]): Unit = {
        System.arraycopy(max, min.length, array, len * 2, max.length - min.length)
      }
      if(self.length > that.length) {
        cp(that.self, self)
      }else if(self.length < that.length){
        cp(self, that.self)
      }
      new $clazz(array)
    }
  }

  def intersperse(a: $a): $clazz = {
    if(self.length == 0){
      $empty
    }else{
      val array = new Array[$a]((self.length * 2) - 1)
      var i = 0
      java.util.Arrays.fill(array$castObj, a)
      while(i < self.length){
        array(i * 2) = self(i)
        i += 1
      }
      new $clazz(array)
    }
  }
}

object $obj {

  def apply${withTag}(elems: $a *): $clazz = {
    new $clazz(elems.toArray)
  }

  ${
    if(a == REF){
      s"def empty${withTag}: $clazz = new $clazz(new Array[$a](0))"
    }else{
      s"val empty: $clazz = new $clazz(new Array[$a](0))"
    }
  }

  def iterate${withTag}(start: $a, len: Int)(f: $a => $a): $clazz = {
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

  def tabulate${withTag}(n: Int)(f: Int => $a): $clazz = {
    val array = new Array[$a](n)
    var i = 0
    while (i < n) {
      array(i) = f(i)
      i += 1
    }
    new $clazz(array)
  }

  def flatten${withTag}(xs: Array[Array[$a]]): $clazz = {
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

  def fillAll${withTag}(size: Int)(elem: $a): $clazz = {
    val array = new Array[$a](size)
    Arrays.fill(array$castObj, elem)
    new $clazz(array)
  }

  def fill$withTag(size: Int)(f: => $a): $clazz = {
    val array = new Array[$a](size)
    var i = 0
    while(i < size){
      array(i) = f
      i += 1
    }
    new $clazz(array)
  }

  def unfold[@specialized B ${if(a == REF) ",X <: AnyRef: reflect.ClassTag" else ""}](z: B)(f: B => Option[(B, $a)]): $clazz = {
    val builder = new ArrayBuilder.$clazz()
    @annotation.tailrec
    def loop(next: Option[(B, $a)]): Unit = next match {
      case Some((b, a)) =>
        builder += a
        loop(f(b))
      case None =>
    }
    loop(f(z))
    new $clazz(builder.result())
  }

}
"""
  }
}

