package nobox

import Generate.list

object WithFilter {

  def apply(a: Type): String = {

    val map0: String = {
      val cases: String = list.map{ b =>
        s"      case ClassTag.$b => map$b(g.asInstanceOf[$a => $b])"
      }.mkString("\n")

s"""
  def map[A](g: $a => A)(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.filter(f).map(g) // TODO
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val map: Type => String = { b =>
      import b._
s"""
  def map$yWithTag(g: $a => $y): Array[$y] = {
    val builder = new ArrayBuilder.of$tparamy()
    var i = 0
    while(i < self.length){
      if(f(self.self(i))){
        builder += g(self.self(i))
      }
      i += 1
    }
    builder.result
  }
"""
    }

    val flatMap: Type => String = { b =>
      import b._
s"""
  def flatMap$yWithTag(g: $a => Array[$y]): Array[$y] = {
    val builder = new ArrayBuilder.of$tparamy()
    var i = 0
    while(i < self.length){
      if(f(self.self(i))){
        val x = g(self.self(i))
        var j = 0
        while(j < x.length){
          builder += x(j)
          j += 1
        }
      }
      i += 1
    }
    builder.result
  }
"""
    }

    val flatMap0: String = {

      val cases: String = list.map{ b =>
        s"      case ClassTag.$b => flatMap$b(g.asInstanceOf[$a => Array[$b]])"
      }.mkString("\n")

s"""
  def flatMap[A](g: $a => Array[A])(implicit A: ClassTag[A]): Array[A] = {
    (A match {
$cases
      case _ => self.filter(f).flatMap(g) // TODO
    }).asInstanceOf[Array[A]]
  }
"""
    }

    val foreach0: String = {
s"""
  def foreach[U](g: $a => U): Unit = {
    var i = 0
    while(i < self.length){
      if(f(self.self(i))){
        g(self.self(i))
      }
      i += 1
    }
  }
"""
    }

    val withFilter0: String = {
s"""
  def withFilter(g: $a => Boolean): WithFilter${a.name + a.tparamx} =
    new WithFilter${a.name + a.tparamx}(self, {a => f(a) && g(a)})
"""
    }

    (List[String](
      map0, flatMap0, foreach0, withFilter0
    ) ::: List[Type => String](
      map,  flatMap
    ).map{ method =>
      list map method mkString "\n"
    }).mkString("\n")
  }

}
