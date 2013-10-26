package nobox

sealed abstract class Type(override val toString: String)

object Type {
  object BOOL   extends Type("Boolean")
  object BYTE   extends Type("Byte")
  object CHAR   extends Type("Char")
  object SHORT  extends Type("Short")
  object INT    extends Type("Int")
  object LONG   extends Type("Long")
  object FLOAT  extends Type("Float")
  object DOUBLE extends Type("Double")
}

