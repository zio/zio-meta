package zio.meta

import izumi.reflect.Tag

trait TypeMoniker[T] {
  import TypeMoniker.*
  def moniker: Moniker
}

object TypeMoniker {
  final case class Moniker(self: MonikerFor[_], params: List[Moniker] = Nil) {
    def name: String = params match {
      case Nil => self.name
      case _   => self.name + params.map(_.name).mkString("[", ", ", "]")
    }
  }
}

final case class MonikerFor[T](tag: Tag[T]) extends AnyVal {
  def name: String = tag.tag.longName
}
