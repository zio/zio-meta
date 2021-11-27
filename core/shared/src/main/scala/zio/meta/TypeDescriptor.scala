package zio.meta

sealed trait TypeDescriptor[+Attribs] {
  import TypeDescriptor.*

  final def attributes: Attribs = caseValue.attributes
  def caseValue: TypeDescriptorCase[Attribs, TypeDescriptor[Attribs]]
}

object TypeDescriptor {
  import TypeDescriptorCase.*

  def reference[Attribs](
      name: String,
      typeParams: List[TypeDescriptor[Attribs]],
      attributes: Attribs
  ): TypeDescriptor[Attribs] =
    Reference(ReferenceCase(name, typeParams, attributes))

  def variable[Attribs](name: String, attributes: Attribs): TypeDescriptor[Attribs] =
    Variable(VariableCase(name, attributes))

  final case class Variable[+Attribs](override val caseValue: TypeDescriptorCase.VariableCase[Attribs])
      extends TypeDescriptor[Attribs] {
    def name: String = caseValue.name
  }

  final case class Reference[+Attribs](override val caseValue: ReferenceCase[Attribs, TypeDescriptor[Attribs]])
      extends TypeDescriptor[Attribs] {
    def name: String = caseValue.name
  }

  sealed trait TypeDescriptorCase[+Attribs, +A] { self =>
    def attributes: Attribs

    def map[B](f: A => B): TypeDescriptorCase[Attribs, B] = self match {
      case c @ VariableCase(_, _)     => c
      case c @ ReferenceCase(_, _, _) => ReferenceCase(c.name, c.args.map(f), attributes)

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
        args: List[A],
        attributes: Attribs
    ) extends TypeDescriptorCase[Attribs, A]
  }
}
