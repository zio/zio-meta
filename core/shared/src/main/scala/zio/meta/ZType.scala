package zio.meta

import zio.meta.ZType.*
import org.w3c.dom.Attr

final case class ZType[+LeafAttributes, +BranchAttributes](
    caseValue: ZTypeCase[LeafAttributes, BranchAttributes, ZType[LeafAttributes, BranchAttributes]]
) { self =>
  import ZTypeCase.*

  def fold[Z](f: ZTypeCase[LeafAttributes, BranchAttributes, Z] => Z): Z =
    self.caseValue match {
      case c @ ModuleCase(name, members, attributes)     => f(ModuleCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeCase(name, members, attributes)       => f(TypeCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeRefCase(name, typeParams, attributes) => f(TypeRefCase(name, typeParams.map(_.fold(f)), attributes))
      case c @ TypeVarCase(_, _)                         => f(c)
      case c @ FunctionCase(name, typeVars, params, returnType, attributes) =>
        f(FunctionCase(name, typeVars.map(_.fold(f)), params.map(_.fold(f)), returnType.fold(f), attributes))
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
      case c @ TypeRefCase(name, typeParams, attributes) =>
        ZType(f(TypeRefCase(name, typeParams.map(_.transform(f)), attributes)))
      case c @ TypeVarCase(_, attributes) => ZType(f(c))
      case c @ FunctionCase(name, typeVars, params, returnType, attributes) =>
        ZType(
          f(
            FunctionCase(
              name,
              typeVars.map(_.transform(f)),
              params.map(_.transform(f)),
              returnType.transform(f),
              attributes
            )
          )
        )
      case c @ FieldCase(_, _) => ZType(f(c))
    }
}

object ZType {
  sealed trait ZTypeCase[+LeafAttributes, +BranchAttributes, +A] { self =>
    import ZTypeCase.*

    def map[B](f: A => B): ZTypeCase[LeafAttributes, BranchAttributes, B] = self match {
      case ModuleCase(name, members, attributes)     => ModuleCase(name, members.map(f), attributes)
      case TypeCase(name, members, attributes)       => TypeCase(name, members.map(f), attributes)
      case TypeRefCase(name, typeParams, attributes) => TypeRefCase(name, typeParams.map(f), attributes)
      case TypeVarCase(name, attributes)             => TypeVarCase(name, attributes)
      case FunctionCase(name, typeVars, params, returnType, attributes) =>
        FunctionCase(name, typeVars.map(f), params.map(f), f(returnType), attributes)
      case FieldCase(name, attributes) => FieldCase(name, attributes)
    }

  }
  // sealed trait ModuleMemberCase[+BranchAttributes, +LeafAttributes, +A] extends ZTypeCase[A]
  object ZTypeCase {
    def typeVar[Attribs](name: Name, attributes: ZContext[Attribs]): ZTypeCase[Attribs, Nothing, Nothing] =
      TypeVarCase(name, attributes)

    def typeRef[Attribs, A](name: Name, typeParams: A*)(
        attributes: ZContext[Attribs]
    ): ZTypeCase[Nothing, Attribs, A] = TypeRefCase(name, typeParams.toList, attributes)

    final case class FunctionCase[+LeafAttributes, +A](
        name: Name,
        typeVars: List[A],
        params: List[A],
        returnType: A,
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

    final case class TypeRefCase[+BranchAttributes, +A](
        name: Name,
        typeParams: List[A],
        attributes: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]

    final case class TypeVarCase[+LeafAttributes](name: Name, attributes: ZContext[LeafAttributes])
        extends ZTypeCase[Nothing, Nothing, Nothing]
    final case class FieldCase[+LeafAttributes](name: Name, attributes: LeafAttributes)
        extends ZTypeCase[LeafAttributes, Nothing, Nothing]
  }
}
