package zio.meta
import zio.meta.CodeStruct.*

final case class CodeStruct[+LeafAttributes, +BranchAttributes](
    caseValue: CodeStructCase[LeafAttributes, BranchAttributes, CodeStruct[LeafAttributes, BranchAttributes]]
) { self =>
  import CodeStructCase.*

  def fold[Z](f: CodeStructCase[LeafAttributes, BranchAttributes, Z] => Z): Z =
    self.caseValue match {
      case c @ BundleCase(modules, attributes)       => f(BundleCase(modules.map(_.fold(f)), attributes))
      case c @ ModuleCase(name, members, attributes) => f(ModuleCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeCase(name, members, attributes)   => f(TypeCase(name, members.map(_.fold(f)), attributes))
      case c @ TypeRefCase(_)                        => f(c)
      case c @ FunctionCase(_, _)                    => f(c)
      case c @ FieldCase(_, _)                       => f(c)
    }

  def transform[LeafAttributes2, BranchAttributes2](
      f: CodeStructCase[
        LeafAttributes,
        BranchAttributes,
        CodeStruct[LeafAttributes2, BranchAttributes2]
      ] => CodeStructCase[LeafAttributes2, BranchAttributes2, CodeStruct[LeafAttributes2, BranchAttributes2]]
  ): CodeStruct[LeafAttributes2, BranchAttributes2] =
    self.caseValue match {
      case c @ BundleCase(modules, attributes) =>
        CodeStruct(f(BundleCase(modules.map(_.transform(f)), attributes))) // CodeStruct(f(c))
      case c @ ModuleCase(name, members, attributes) =>
        CodeStruct(f(ModuleCase(name, members.map(_.transform(f)), attributes))) // CodeStruct(f(c))
      case c @ TypeCase(name, members, attributes) =>
        CodeStruct(f(TypeCase(name, members.map(_.transform(f)), attributes))) // CodeStruct(f(c))
      case c @ TypeRefCase(_)     => CodeStruct(f(c))
      case c @ TypeVarCase(_)     => CodeStruct(f(c))
      case c @ FunctionCase(_, _) => CodeStruct(f(c))
      case c @ FieldCase(_, _)    => CodeStruct(f(c))
    }
}

object CodeStruct {

  sealed trait CodeStructCase[+LeafAttributes, +BranchAttributes, +A] { self =>

    def map[B](f: A => B): CodeStructCase[LeafAttributes, BranchAttributes, B] = ???

  }
  // sealed trait ModuleMemberCase[+BranchAttributes, +LeafAttributes, +A] extends CodeStructCase[A]
  object CodeStructCase {
    def typeVar(name: Name): CodeStructCase[Nothing, Nothing, Nothing] = TypeVarCase(name)

    final case class BundleCase[+BranchAttributes, +A](module: List[A], attributes: BranchAttributes)
        extends CodeStructCase[Nothing, BranchAttributes, A]
    final case class FunctionCase[+LeafAttributes](name: Name, atributes: LeafAttributes)
        extends CodeStructCase[LeafAttributes, Nothing, Nothing]
    final case class ModuleCase[+BranchAttributes, +A](name: Name, members: List[A], attributes: BranchAttributes)
        extends CodeStructCase[Nothing, BranchAttributes, A]
    final case class TypeCase[+BranchAttributes, +A](name: Name, members: List[A], attributes: BranchAttributes)
        extends CodeStructCase[Nothing, BranchAttributes, A]
    final case class TypeRefCase(name: Name) extends CodeStructCase[Nothing, Nothing, Nothing]
    final case class TypeVarCase(name: Name) extends CodeStructCase[Nothing, Nothing, Nothing]
    final case class FieldCase[+LeafAttributes](name: Name, attributes: LeafAttributes)
        extends CodeStructCase[LeafAttributes, Nothing, Nothing]
  }
}

final case class CodeLibrary[+LeafAttributes, +BranchAttributes](
    name: Name,
    structures: List[CodeStruct[LeafAttributes, BranchAttributes]]
)

// object example {
//   import izumi.reflect.Tag
//   type TypeA
//   type TypeB

//   type Structure[A]
//   def codeStructure[A: Tag]: Structure[A]

//   val csA = codeStructure[TypeA]
//   val csB = codeStructure[TypeB]

//   val all = csA && csB && csC

//   final case class Attributed[+Attrib, +A](attributes: Attrib, value: A)

// }
