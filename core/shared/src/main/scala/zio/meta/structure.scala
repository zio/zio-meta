package zio.meta

//object structure extends StructureVersionSpecific

sealed trait Structure[T]
object Structure extends StructureVersionSpecific {
  final case class TypeVar(name: String) extends Structure[Nothing]
  final case class TypeStructure[T](name: String, fullName: String, typeParams: List[TypeVar]) extends Structure[T] {
    def show: String = {
      val params = typeParams.map(_.name).mkString("[", ", ", "]")
      s"$name$params"
    }
  }
}


