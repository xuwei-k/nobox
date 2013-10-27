package nobox

// scala.collection.mutable.ArrayBuilder does not have `++=(xs: Array)`
object ArrayBuilder{

  def apply(a: String): String = {

s"""package nobox

private final class ArrayBuilder$a {

  private[this] var elems: Array[$a] = _
  private[this] var capacity: Int = 0
  private[this] var size: Int = 0

  private def mkArray(size: Int): Array[$a] = {
    val newelems = new Array[$a](size)
    if (this.size > 0) Array.copy(elems, 0, newelems, 0, this.size)
    newelems
  }

  private def resize(size: Int) {
    elems = mkArray(size)
    capacity = size
  }

  def sizeHint(size: Int) {
    if (capacity < size) resize(size)
  }

  private def ensureSize(size: Int) {
    if (capacity < size || capacity == 0) {
      var newsize = if (capacity == 0) 16 else capacity * 2
      while (newsize < size) newsize *= 2
      resize(newsize)
    }
  }

  def +=(elem: $a): this.type = {
    ensureSize(size + 1)
    elems(size) = elem
    size += 1
    this
  }

  def ++=(xs: Array[$a]): this.type = {
    ensureSize(this.size + xs.length)
    System.arraycopy(xs, 0, elems, this.size, xs.length)
    size += xs.length
    this
  }

  def result(): of$a = new of$a(
    if (capacity != 0 && capacity == size) elems
    else mkArray(size)
  )

}
"""

 }
}

