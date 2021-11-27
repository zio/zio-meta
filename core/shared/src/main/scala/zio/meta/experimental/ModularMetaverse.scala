package zio.meta

trait ModularMetaverse {
  final case class Library(
      name: String,
      description: String,
      version: String,
      dependencies: List[String],
      modules: List[Module]
  )

  final case class Module(
      name: String,
      description: String,
      version: String,
      imports: List[String],
      exports: List[ModuleMember]
  )

  final case class ModuleMember(caseValue: ModuleMember.ModuleMemberCase[ModuleMember])
  object ModuleMember {
    sealed trait ModuleMemberCase[+A] { self => }
    object ModuleMemberCase {
      final case class Field[A](name: String, description: String, typeName: String, default: Option[A])
          extends ModuleMemberCase[A]
    }
  }

  final case class Type[+A](attributes: A) { self =>
    def withAttributes[B](attributes: B): Type[B] = Type(attributes)
  }
  object Type {
    sealed trait TypeCase[+A] { self => }
    object TypeCase {
      final case class Variable()
    }
  }
  // sealed trait Descriptor[+A]
  // object Descriptor {
  //   final case class Bundle[+A]()              extends Descriptor[A]
  //   final case class Single[+A](attributes: A) extends Descriptor[A]
  // }
}
