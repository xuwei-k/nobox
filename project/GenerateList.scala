package nobox

import sbt._
import nobox.Generate.list

object GenerateList{

  def apply(dir: File): Seq[File] = {
    list.map{ t =>
      val f = dir / (t + "List.scala")
      IO.write(f, src(t))
      f
    }
  }

  def src(a: String): String = {

    def clazz(s: String) = s + "List"
    def cons(s: String) = s + "Cons"
    def nil(s: String) = s + "Nil"

    val (listA, consA, nilA) = (clazz(a), cons(a), nil(a))

    val map: String => String = { b =>
      val (listB, consB, nilB) = (clazz(b), cons(b), nil(b))
s"""
  def map$b(f: $a => $b): $listB =
    foldRight($listB.nil)((a, acc) => new $consB(f(a), acc))
"""
    }

    val flatMap: String => String = { b =>
      val (listB, consB, nilB) = (clazz(b), cons(b), nil(b))
s"""
  def flatMap$b(f: $a => $listB): $listB = ???
"""
    }

    val methods: String = List(
      map, flatMap
    ).map{ method =>
      list map method mkString "\n"
    }.mkString("\n\n")

s"""package nobox

import scala.collection.mutable.ArrayBuilder
import annotation.tailrec
import $listA.nil

sealed abstract class $listA extends Product with Serializable{

  def headOption: Option[$a] = this match {
    case $consA(h, t) => Some(h)
    case $nilA => None
  }

  def tailOption: Option[$listA] = this match {
    case $consA(h, t) => Some(t)
    case $nilA => None
  }

  final def reverse: $listA = {
    var result = nil
    var these = this
    var empty = false
    while(!empty) {
      these match{
        case $consA(h, t) =>
          result = new $consA(h, result)
          these = t
        case $nilA =>
          empty = true
      }
    }
    result
  }

  final def ::(head: $a): $listA = new $consA(head, this)

  def toList: List[$a] =
    foldRight[List[$a]](Nil)(_ :: _)

  def toArray: Array[$a] = {
    val builder = new ArrayBuilder.of$a
    var these = this
    var empty = false
    while(!empty){
      these match{
        case $consA(h, t) =>
          builder += h
          these = t
        case $nilA =>
          empty = true
      }
    }
    builder.result
  }

  def foldRight[B](z: B)(f: ($a, B) => B): B =
    reverse.foldLeft(z)((a, acc) => f(acc, a))

  @tailrec
  final def foldLeft[B](z: B)(f: (B, $a) => B): B = this match {
    case $consA(h, t) => t.foldLeft(f(z, h))(f)
    case $nilA => z
  }

  def isEmpty: Boolean

  def length: Int = {
    var these = this
    var empty = false
    var n = 0
    while(!empty){
      these match{
        case $consA(_, t) =>
          n += 1
          these = t
        case $nilA =>
          empty = true
      }
    }
    n
  }

  def ===(that: $listA): Boolean = this == that

  // TODO optimize
  def mkString(start: String, sep: String, end: String): String =
    toList.mkString(start, sep, end)

  $methods
}

object $listA{
  
  def apply(elems: $a *): $listA =
    elems.reverse.foldLeft(nil){case (acc, a) => new $consA(a, acc)}

  @inline def nil: $listA = $nilA
}

final case class $consA private[nobox](head: $a, tail: $listA) extends $listA {

  def isEmpty = false

  // TODO optimize
  override def toString = toList.mkString("$listA(",", ",")")
}

final case object $nilA extends $listA {

  def isEmpty = true

}
"""
  }
  
}
