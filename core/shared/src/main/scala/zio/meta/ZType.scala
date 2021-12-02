package zio.meta

import zio.meta.ZType.*

final case class ZType[+LeafAttributes, +BranchAttributes](
    caseValue: ZTypeCase[LeafAttributes, BranchAttributes, ZType[LeafAttributes, BranchAttributes]]
) { self =>
  import ZTypeCase.*

  def fold[Z](f: ZTypeCase[LeafAttributes, BranchAttributes, Z] => Z): Z =
    self.caseValue match {
      case c @ ModuleCase(name, members, attributes) => f(ModuleCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeCase(name, members, attributes)   => f(TypeCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeRefCase(_)                        => f(c)
      case c @ FunctionCase(name, typeVars, attributes) =>
        f(FunctionCase(name, typeVars.map(_.fold(f)), attributes))
      case c @ FieldCase(_, _) => f(c)
    }

  def transform[LeafAttributes2, BranchAttributes2](
      f: ZTypeCase[
        LeafAttributes,
        BranchAttributes,
        ZType[LeafAttributes2, BranchAttributes2]
      ] => ZTypeCase[LeafAttributes2, BranchAttributes2, ZType[LeafAttributes2, BranchAttributes2]]
  ): ZType[LeafAttributes2, BranchAttributes2] =
    self.caseValue match {
      case c @ ModuleCase(name, members, attributes) =>
        ZType(f(ModuleCase(name, members.map(_.transform(f)), attributes))) // ZType(f(c))
      case c @ TypeCase(name, members, attributes) =>
        ZType(f(TypeCase(name, members.map(_.transform(f)), attributes))) // ZType(f(c))
      case c @ TypeRefCase(_) => ZType(f(c))
      case c @ TypeVarCase(_) => ZType(f(c))
      case c @ FunctionCase(name, typeVars, attributes) =>
        ZType(f(FunctionCase(name, typeVars.map(_.transform(f)), attributes)))
      case c @ FieldCase(_, _) => ZType(f(c))
    }
}

object ZType {
  sealed trait ZTypeCase[+LeafAttributes, +BranchAttributes, +A] { self =>
    import ZTypeCase.*

    def map[B](f: A => B): ZTypeCase[LeafAttributes, BranchAttributes, B] = self match {
      case ModuleCase(name, members, attributes)    => ModuleCase(name, members.map(f), attributes)
      case TypeCase(name, members, attributes)      => TypeCase(name, members.map(f), attributes)
      case TypeRefCase(name)                        => TypeRefCase(name)
      case TypeVarCase(name)                        => TypeVarCase(name)
      case FunctionCase(name, typeVars, attributes) => FunctionCase(name, typeVars.map(f), attributes)
      case FieldCase(name, attributes)              => FieldCase(name, attributes)
    }

  }
  // sealed trait ModuleMemberCase[+BranchAttributes, +LeafAttributes, +A] extends ZTypeCase[A]
  object ZTypeCase {
    def typeVar(name: Name): ZTypeCase[Nothing, Nothing, Nothing] = TypeVarCase(name)

    final case class FunctionCase[+LeafAttributes, +A](
        name: Name,
        typeVars: List[A],
        atributes: ZContext[LeafAttributes]
    ) extends ZTypeCase[LeafAttributes, Nothing, A]
    final case class ModuleCase[+BranchAttributes, +A](
        name: Name,
        members: List[A],
        attributes: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]
    final case class TypeCase[+BranchAttributes, +A](
        name: Name,
        members: List[A],
        attributes: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]
    final case class TypeRefCase(name: Name) extends ZTypeCase[Nothing, Nothing, Nothing]

    final case class TypeVarCase(name: Name) extends ZTypeCase[Nothing, Nothing, Nothing]
    final case class FieldCase[+LeafAttributes](name: Name, attributes: LeafAttributes)
        extends ZTypeCase[LeafAttributes, Nothing, Nothing]
  }
}
