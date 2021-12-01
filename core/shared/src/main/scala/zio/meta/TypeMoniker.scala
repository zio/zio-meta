package zio.meta

import izumi.reflect.Tag

trait TypeMoniker[T] {
  import TypeMoniker.*
  def moniker: Moniker[T]
}

object TypeMoniker {
  final case class Moniker[T](self: MonikerFor[T], params: List[Moniker[?]] = Nil) {
    def name: String = params match {
      case Nil => self.name
      case _   => self.name + params.map(_.name).mkString("[", ", ", "]")
    }
  }
}

sealed trait MonikerFor[+T] {
  def name: String
}

object MonikerFor {
  def apply(name: String): MonikerFor[Nothing] = WithoutTag(name)

  def fromTag[T](tag: Tag[T]): MonikerFor[T] = FromTag(tag)

  final case class WithoutTag(name: String) extends MonikerFor[Nothing]
  final case class FromTag[T](tag: Tag[T]) extends MonikerFor[T] {
    def name: String = tag.tag.longName
  }
}
