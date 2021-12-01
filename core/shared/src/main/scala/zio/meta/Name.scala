package zio.meta
import Name.NameCase
import Name.NameCase.*

final case class Name(caseValue: NameCase[Name]) { self =>
  // def fold[Z](f:NameCase[Z], Z):Z = caseValue match {
  //   case
  // }
}

object Name {
  def typeParam(text: String): Name =
    Name(TypeParamCase(text, Variance.Invariant))
  def typeParam(text: String, variance: Variance): Name =
    Name(TypeParamCase(text, variance))

  def identifier(text: String): Name = Name(IdentifierCase(text))
  def namespace(root: String, rest: String*): Name = {
    val segments = (root :: rest.toList).map(text => Name(IdentifierCase(text)))
    Name(NamespaceCase(segments))
  }

  sealed trait MemberNameKind
  object MemberNameKind {
    case object Constructor extends MemberNameKind
    case object Field       extends MemberNameKind
    case object Method      extends MemberNameKind
    case object Type        extends MemberNameKind
  }

  sealed trait Variance
  object Variance {
    case object Covariant     extends Variance
    case object Contravariant extends Variance
    case object Invariant     extends Variance
  }

  sealed trait NameCase[+A] { self =>
    import NameCase.*

    def map[B](f: A => B): NameCase[B] =
      self match {
        case AnonymousCase           => AnonymousCase
        case c @ IdentifierCase(_)   => c
        case c @ TypeParamCase(_, _) => c
        case c @ TopLevelCase(_, _)  => c
        case c @ MemberCase(_, _, _) => MemberCase(c.text, c.kind, f(c.parent))
        case c @ NamespaceCase(_)    => NamespaceCase(c.segments.map(f))
        case c @ QualifiedCase(_, _) => QualifiedCase(f(c.namespace), f(c.localName))
      }
  }
  object NameCase {
    // TODO: Flesh this out a bit more
    case object AnonymousCase                                                               extends NameCase[Nothing]
    final case class IdentifierCase(text: String)                                           extends NameCase[Nothing]
    final case class TypeParamCase(text: String, variance: Variance)                        extends NameCase[Nothing]
    final case class TypeNameCase[+A](text: String, parent: Option[A], typeParams: List[A]) extends NameCase[A]
    final case class TopLevelCase(text: String, kind: MemberNameKind)                       extends NameCase[Nothing]
    final case class MemberCase[+A](text: String, kind: MemberNameKind, parent: A)          extends NameCase[A]
    final case class NamespaceCase[+A](segments: List[A])                                   extends NameCase[A]
    object NamespaceCase {
      def apply[A](root: A, rest: A*): NamespaceCase[A] = NamespaceCase(root :: rest.toList)
    }
    final case class QualifiedCase[+A](namespace: A, localName: A) extends NameCase[A]
  }
}
