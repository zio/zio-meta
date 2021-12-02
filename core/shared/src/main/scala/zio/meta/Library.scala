package zio.meta

import zio.meta.ZType.*

final case class Library(
    name: Name,
    types: List[ZType[?, ?]]
) { self =>
  def ::[LA, BA](zType: ZType[LA, BA]): Library = self.copy(types = zType :: types)
}

object Library {
  def empty(name: Name): Library = Library(name: Name, List.empty)
}
