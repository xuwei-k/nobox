package nobox

object Info {
  def main(args: Array[String]): Unit = {
    val classes = List[Class[_]](
      classOf[ofInt],
      classOf[ofLong],
      classOf[ofBoolean],
      classOf[ofByte],
      classOf[ofShort],
      classOf[ofChar],
      classOf[ofFloat],
      classOf[ofDouble],
      classOf[ofRef[_]]
    )

    val companions = List[Class[_]](
      ofInt.getClass,
      ofLong.getClass,
      ofBoolean.getClass,
      ofByte.getClass,
      ofShort.getClass,
      ofChar.getClass,
      ofFloat.getClass,
      ofDouble.getClass,
      ofRef.getClass
    )

    val exclude = classOf[AnyRef].getMethods.map(_.getName).toSet

    val methods = List(classes, companions).flatten.map{ c =>
      c -> c.getMethods.map(_.getName).filterNot(m => (m contains '$') || exclude(m))
    }

    if(!args.isEmpty){
      methods.foreach{ case (c, m) =>
        println(c.toString + " " + m.size)
        println(m.mkString(", "))
        println()
      }
    }else{
      methods.foreach{ case (c, m) =>
        println(c.toString + " " + m.size)
      }
    }
  }
}


