package nobox

import nobox.Type._

object Methods{

  def apply(a: Type): String = {

    val clazz = "of" + a.name + a.tparamx

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
      case REF =>
s"""
  def sum(implicit X: Numeric[X]): $a = {
    var i = 0
    var n = X.zero
    while(i < self.length){
      n = X.plus(n, self(i))
      i += 1
    }
    n
  }
"""
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
      case BOOL => ""
      case REF =>
s"""
  def sorted(implicit ord: Ordering[X]): $clazz = {
    val array = self.clone
    Arrays.sort(array.asInstanceOf[Array[Object]], ord.asInstanceOf[Ordering[Object]])
    new $clazz(array)
  }
"""
      case _ =>
s"""
  def sorted: $clazz = {
    val array = self.clone
    Arrays.sort(array)
    new $clazz(array)
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

    val flatten: String = {
      if(a != REF){ ""
      }else{
        val cases: String = Generate.list.map{ b =>
          s"      case Clazz.$b => of$b.flatten(self.asInstanceOf[Array[Array[$b]]]).self"
        }.mkString("\n")
s"""
  def flatten[E](implicit E: X <:< Array[E]): Array[E] =
    (self.getClass.getComponentType.getComponentType match {
$cases
      case c => ofRef.flatten[E with AnyRef](self.asInstanceOf[Array[Array[E with AnyRef]]])(ClassTag(c)).self
    }).asInstanceOf[Array[E]]
"""
      }
    }

    List[String](
      sum, sumLong, product, productLong, productDouble, sorted, maxAndMin, flatten
    ).mkString("\n\n")

  }
}

