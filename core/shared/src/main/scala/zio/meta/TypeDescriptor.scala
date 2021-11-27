package zio.meta

sealed trait TypeDescriptor[+Attribs] { self =>
  import TypeDescriptor.*
  import TypeDescriptor.TypeDescriptorCase.*

  final def attributes: Attribs = caseValue.attributes
  def caseValue: TypeDescriptorCase[Attribs, TypeDescriptor[Attribs]]

  def fold[Z](f: TypeDescriptorCase[Attribs, Z] => Z): Z = caseValue match {
    case c @ VariableCase(_, _) => f(c)
    case c @ ReferenceCase(_, _, _) =>
      val typeParams = c.typeParams.map(_.fold(f))
      f(ReferenceCase(c.name, typeParams, c.attributes))
  }

  def mapAttributes[Attribs1](f: Attribs => Attribs1): TypeDescriptor[Attribs1] = transform {
    case c @ VariableCase(_, _) => VariableCase(c.name, f(c.attributes))
    case c @ ReferenceCase(_, _, _) =>
      ReferenceCase(c.name, c.typeParams, f(c.attributes))
  }

  def transform[Attribs1](
      f: TypeDescriptorCase[Attribs, TypeDescriptor[Attribs1]] => TypeDescriptorCase[Attribs1, TypeDescriptor[Attribs1]]
  ): TypeDescriptor[Attribs1] = caseValue match {
    case c @ VariableCase(_, _) => TypeDescriptor(f(c))
    case c @ ReferenceCase(_, _, _) =>
      val typeParams = c.typeParams.map(_.transform(f))
      TypeDescriptor(f(ReferenceCase(c.name, typeParams, c.attributes)))
  }
}

object TypeDescriptor {
  import TypeDescriptorCase.*

  def apply[Attribs](caseValue: TypeDescriptorCase[Attribs, TypeDescriptor[Attribs]]): TypeDescriptor[Attribs] =
    caseValue match {
      case c @ VariableCase(_, _)                       => Variable(c)
      case c @ ReferenceCase(name, typeParams, attribs) => Reference(c)
    }

  def reference(name: String): TypeDescriptor[Unit] = reference(name, Nil, ())
  def reference(name: String, typeParams: List[TypeDescriptor[Unit]]): TypeDescriptor[Unit] =
    Reference(ReferenceCase(name, typeParams, ()))

  def reference[Attribs](
      name: String,
      typeParams: List[TypeDescriptor[Attribs]],
      attributes: Attribs
  ): TypeDescriptor[Attribs] =
    Reference(ReferenceCase(name, typeParams, attributes))

  def variable(name: String): TypeDescriptor[Unit] =
    Variable(VariableCase(name, ()))

  def variable[Attribs](name: String, attributes: Attribs): TypeDescriptor[Attribs] =
    Variable(VariableCase(name, attributes))

  final case class Variable[+Attribs](override val caseValue: TypeDescriptorCase.VariableCase[Attribs])
      extends TypeDescriptor[Attribs] {
    def name: String = caseValue.name
  }

  final case class Reference[+Attribs](override val caseValue: ReferenceCase[Attribs, TypeDescriptor[Attribs]])
      extends TypeDescriptor[Attribs] {
    def name: String                              = caseValue.name
    def typeParams: List[TypeDescriptor[Attribs]] = caseValue.typeParams
  }

  sealed trait TypeDescriptorCase[+Attribs, +A] { self =>
    def attributes: Attribs

    def map[B](f: A => B): TypeDescriptorCase[Attribs, B] = self match {
      case c @ VariableCase(_, _)     => c
      case c @ ReferenceCase(_, _, _) => ReferenceCase(c.name, c.typeParams.map(f), attributes)

    }

    def mapAttributes[Attribs1](f: Attribs => Attribs1): TypeDescriptorCase[Attribs1, A] = self match {
      case VariableCase(name, attribs)        => VariableCase(name, f(attribs))
      case ReferenceCase(name, args, attribs) => ReferenceCase(name, args, f(attribs))
    }
  }
  object TypeDescriptorCase {
    def variable[Attribs](name: String, attributes: Attribs): TypeDescriptorCase[Attribs, Nothing] =
      VariableCase(name, attributes)

    def reference[Attribs, A](name: String, args: List[A], attributes: Attribs): TypeDescriptorCase[Attribs, A] =
      ReferenceCase(name, args, attributes)

    final case class VariableCase[+Attribs](name: String, attributes: Attribs)
        extends TypeDescriptorCase[Attribs, Nothing]

    final case class ReferenceCase[+Attribs, +A](
        name: String,
        typeParams: List[A],
        attributes: Attribs
    ) extends TypeDescriptorCase[Attribs, A]
  }
}
