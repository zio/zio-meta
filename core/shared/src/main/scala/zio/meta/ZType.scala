package zio.meta

import zio.meta.ZType.*

sealed trait ZModule[+LeafAttributes, BranchAttributes] extends ZType[LeafAttributes, BranchAttributes] { self =>
  def caseValue: ZTypeCase.ModuleCase[BranchAttributes, ZType[LeafAttributes, BranchAttributes]]
}

object ZModule {
  def apply[LeafAttributes, BranchAttributes](
      caseVal: ZTypeCase.ModuleCase[BranchAttributes, ZType[LeafAttributes, BranchAttributes]]
  ): ZModule[LeafAttributes, BranchAttributes] =
    new ZModule[LeafAttributes, BranchAttributes] {
      override def caseValue: ZTypeCase.ModuleCase[BranchAttributes, ZType[LeafAttributes, BranchAttributes]] = caseVal
    }

  def apply[LeafAttributes, BranchAttributes](
      name: Name,
      members: List[ZType[LeafAttributes, BranchAttributes]],
      ctx: ZContext[BranchAttributes]
  ): ZModule[LeafAttributes, BranchAttributes] =
    new ZModule {
      def caseValue: ZTypeCase.ModuleCase[BranchAttributes, ZType[LeafAttributes, BranchAttributes]] =
        ZTypeCase.ModuleCase(name, members, ctx)
    }

  def unapply[LeafAttributes, BranchAttributes](
      zType: ZType[LeafAttributes, BranchAttributes]
  ): Option[ZTypeCase.ModuleCase[BranchAttributes, ZType[LeafAttributes, BranchAttributes]]] = zType.caseValue match {
    case ZTypeCase.ModuleCase(name, members, ctx) => Some(ZTypeCase.ModuleCase(name, members, ctx))
    case _                                        => None
  }
}

sealed trait ZType[+LeafAttributes, +BranchAttributes] { self =>
  import ZTypeCase.*

  def caseValue: ZTypeCase[LeafAttributes, BranchAttributes, ZType[LeafAttributes, BranchAttributes]]

  def fold[Z](f: ZTypeCase[LeafAttributes, BranchAttributes, Z] => Z): Z =
    self.caseValue match {
      case c @ ModuleCase(name, members, ctx)     => f(ModuleCase(name, members.map(_.fold(f)), ctx))
      case c @ TypeCase(name, members, ctx)       => f(TypeCase(name, members.map(_.fold(f)), ctx))
      case c @ TypeRefCase(name, typeParams, ctx) => f(TypeRefCase(name, typeParams.map(_.fold(f)), ctx))
      case c @ TypeVarCase(_, _)                  => f(c)
      case c @ FunctionCase(name, typeVars, params, returnType, ctx) =>
        f(FunctionCase(name, typeVars.map(_.fold(f)), params.map(_.fold(f)), returnType.fold(f), ctx))
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
      case c @ ModuleCase(name, members, ctx) =>
        ZType(f(ModuleCase(name, members.map(_.transform(f)), ctx))) // ZType(f(c))
      case c @ TypeCase(name, members, ctx) =>
        ZType(f(TypeCase(name, members.map(_.transform(f)), ctx))) // ZType(f(c))
      case c @ TypeRefCase(name, typeParams, ctx) =>
        ZType(f(TypeRefCase(name, typeParams.map(_.transform(f)), ctx)))
      case c @ TypeVarCase(_, ctx) => ZType(f(c))
      case c @ FunctionCase(name, typeVars, params, returnType, ctx) =>
        ZType(
          f(
            FunctionCase(
              name,
              typeVars.map(_.transform(f)),
              params.map(_.transform(f)),
              returnType.transform(f),
              ctx
            )
          )
        )
      case c @ FieldCase(_, _) => ZType(f(c))
    }
}

object ZType {

  def apply[LeafAttributes, BranchAttributes](
      caseVal: ZTypeCase[LeafAttributes, BranchAttributes, ZType[LeafAttributes, BranchAttributes]]
  ): ZType[LeafAttributes, BranchAttributes] =
    new ZType {
      val caseValue: ZTypeCase[LeafAttributes, BranchAttributes, ZType[LeafAttributes, BranchAttributes]] = caseVal
    }

  object module {
    def apply[LeafAttributes, BranchAttributes](
        name: Name,
        members: List[ZType[LeafAttributes, BranchAttributes]],
        ctx: ZContext[BranchAttributes]
    ): ZType[LeafAttributes, BranchAttributes] =
      ZModule(ZTypeCase.ModuleCase(name, members, ctx))
  }

  sealed trait ZTypeCase[+LeafAttributes, +BranchAttributes, +A] { self =>
    import ZTypeCase.*

    def map[B](f: A => B): ZTypeCase[LeafAttributes, BranchAttributes, B] = self match {
      case ModuleCase(name, members, ctx)     => ModuleCase(name, members.map(f), ctx)
      case TypeCase(name, members, ctx)       => TypeCase(name, members.map(f), ctx)
      case TypeRefCase(name, typeParams, ctx) => TypeRefCase(name, typeParams.map(f), ctx)
      case TypeVarCase(name, ctx)             => TypeVarCase(name, ctx)
      case FunctionCase(name, typeVars, params, returnType, ctx) =>
        FunctionCase(name, typeVars.map(f), params.map(f), f(returnType), ctx)
      case FieldCase(name, ctx) => FieldCase(name, ctx)
    }

  }
  // sealed trait ModuleMemberCase[+BranchAttributes, +LeafAttributes, +A] extends ZTypeCase[A]
  object ZTypeCase {
    def typeVar[Attribs](name: Name, ctx: ZContext[Attribs]): ZTypeCase[Attribs, Nothing, Nothing] =
      TypeVarCase(name, ctx)

    def typeRef[Attribs, A](name: Name, typeParams: A*)(
        ctx: ZContext[Attribs]
    ): ZTypeCase[Nothing, Attribs, A] = TypeRefCase(name, typeParams.toList, ctx)

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
        ctx: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]

    final case class TypeCase[+BranchAttributes, +A](
        name: Name,
        members: List[A],
        ctx: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]

    final case class TypeRefCase[+BranchAttributes, +A](
        name: Name,
        typeParams: List[A],
        ctx: ZContext[BranchAttributes]
    ) extends ZTypeCase[Nothing, BranchAttributes, A]

    final case class TypeVarCase[+LeafAttributes](name: Name, ctx: ZContext[LeafAttributes])
        extends ZTypeCase[Nothing, Nothing, Nothing]
    final case class FieldCase[+LeafAttributes](name: Name, ctx: LeafAttributes)
        extends ZTypeCase[LeafAttributes, Nothing, Nothing]
  }
}
