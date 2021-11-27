package zio.meta

// final case class Descriptor(caseValue: DescriptorCase[Descriptor]) { self =>
//   def fold[Z](f: DescriptorCase[Z] => Z): Z = caseValue match {
//     case c @ DescriptorCase.TypeDescriptor(_) => f(c)
//   }

// }

// object Descriptor {
//   sealed trait DescriptorCase[+A] { self =>
//     import DescriptorCase.*

//     def map[B](f: A => B): DescriptorCase[B] = self match {
//       case TypeDescriptor(name) => TypeDescriptor(name)
//       case _                    => ???
//     }
//   }

//   object DescriptorCase {
//     def typeDescriptor(name: String): DescriptorCase[Nothing] = TypeDescriptor(name)

//     final case class TypeDescriptor(name: String)                               extends DescriptorCase[Nothing]
//     final case class FunctionDescriptor[A](name: Option[String], args: List[A]) extends DescriptorCase[Nothing]
//   }
// }

object descriptor {
  object standard {
    sealed trait TypeDescriptor[+Attribs]
    object TypeDescriptor {
      final case class Variable[+Attribs](name: String, attributes: Attribs) extends TypeDescriptor[Attribs]
      final case class Reference[+Attribs](name: String, typeVars: List[TypeDescriptor[Attribs]], attributes: Attribs)
          extends TypeDescriptor[Attribs]
      final case class Function[+Attribs](
          name: Option[String],
          args: List[TypeDescriptor[Attribs]],
          returnType: TypeDescriptor[Attribs],
          attributes: Attribs
      ) extends TypeDescriptor[Attribs]
    }
  }
  object recursion {
    import Descriptor.*
    import DescriptorCase.FunctionCase

    final case class Func[Attribs](caseValue: FunctionCase[Attribs, Descriptor[Attribs]])

    final case class Descriptor[+Attribs](caseValue: DescriptorCase[Attribs, Descriptor[Attribs]]) { self =>
      def fold[Z](f: DescriptorCase[Attribs, Z] => Z): Z = caseValue match {
        case c @ DescriptorCase.VariableCase(_, _) => f(c)
        case c @ DescriptorCase.ReferenceCase(_, _, _) =>
          val typeArgs = c.typeVars.map(_.fold(f))
          f(DescriptorCase.ReferenceCase(c.name, typeArgs, c.attributes))
        case c @ DescriptorCase.FunctionCase(_, _, _, _) =>
          val args       = c.args.map(_.fold(f))
          val returnType = c.returnType.fold(f)
          f(DescriptorCase.FunctionCase(c.name, args, returnType, c.attributes))
      }
    }
    object Descriptor {
      sealed trait DescriptorCase[+Attribs, +A] { self =>
        import DescriptorCase.*

        def map[B](f: A => B): DescriptorCase[Attribs, B] = self match {
          case c @ VariableCase(_, _)     => c
          case c @ ReferenceCase(_, _, _) => ReferenceCase(c.name, c.typeVars.map(f), c.attributes)
          case c @ FunctionCase(_, _, _, _) =>
            FunctionCase(c.name, c.args.map(f), f(c.returnType), c.attributes)
        }
      }

      object DescriptorCase {
        // def typeDescriptor(name: String): DescriptorCase[Nothing] = ???
        final case class VariableCase[+Attribs](name: String, attributes: Attribs)
            extends DescriptorCase[Attribs, Nothing]
        final case class ReferenceCase[+Attribs, A](name: String, typeVars: List[A], attributes: Attribs)
            extends DescriptorCase[Attribs, A]
        final case class FunctionCase[+Attribs, A](
            name: Option[String],
            args: List[A],
            returnType: A,
            attributes: Attribs
        ) extends DescriptorCase[Attribs, A]
      }
    }

    // final case class OperationInfo[Attribs, A](name: String, func: Descriptor.FunctionCase[Attribs, A])
  }
}
object recursionPlayground {
  import Descriptor.*
  import DescriptorCase.FunctionCase

  final case class OperationInfo[+Attribs](name: String, func: Func[Attribs])

  sealed trait Descriptor[+Attribs] {
    def caseValue: DescriptorCase[Attribs, Descriptor[Attribs]]

    def fold[Z](f: DescriptorCase[Attribs, Z] => Z): Z = caseValue match {
      case c @ DescriptorCase.VariableCase(_, _) => f(c)
      case c @ DescriptorCase.ReferenceCase(_, _, _) =>
        val typeArgs = c.typeVars.map(_.fold(f))
        f(DescriptorCase.ReferenceCase(c.name, typeArgs, c.attributes))
      case c @ DescriptorCase.FunctionCase(_, _, _, _) =>
        val args       = c.args.map(_.fold(f))
        val returnType = c.returnType.fold(f)
        f(DescriptorCase.FunctionCase(c.name, args, returnType, c.attributes))
    }

    def transform[Attribs1 >: Attribs](
        f: DescriptorCase[Attribs, Descriptor[Attribs1]] => DescriptorCase[Attribs1, Descriptor[Attribs1]]
    ): Descriptor[Attribs1] = caseValue match {
      case c @ DescriptorCase.VariableCase(_, _) => Descriptor(f(c))
      case c @ DescriptorCase.ReferenceCase(_, _, _) =>
        val typeArgs = c.typeVars.map(_.transform(f))
        Descriptor(f(DescriptorCase.ReferenceCase(c.name, typeArgs, c.attributes)))
      case c @ DescriptorCase.FunctionCase(_, _, _, _) =>
        val args       = c.args.map(_.transform(f))
        val returnType = c.returnType.transform(f)
        Descriptor(f(DescriptorCase.FunctionCase(c.name, args, returnType, c.attributes)))
    }
  }
  sealed trait Func[+Attribs] extends Descriptor[Attribs] {
    def caseValue: FunctionCase[Attribs, Descriptor[Attribs]]
  }
  object Func {
    def apply[Attribs](caseValue: FunctionCase[Attribs, Descriptor[Attribs]]): Func[Attribs] =
      FuncInstance(caseValue)

    final case class FuncInstance[+Attribs](caseValue: FunctionCase[Attribs, Descriptor[Attribs]]) extends Func[Attribs]
  }

  // final case class Func[+Attribs](caseValue: FunctionCase[Attribs, Descriptor[Attribs]])

  // final case class Descriptor[+Attribs](caseValue: DescriptorCase[Attribs, Descriptor[Attribs]]) { self =>
  //   def fold[Z](f: DescriptorCase[Attribs, Z] => Z): Z = caseValue match {
  //     case c @ DescriptorCase.VariableCase(_, _) => f(c)
  //     case c @ DescriptorCase.ReferenceCase(_, _, _) =>
  //       val typeArgs = c.typeVars.map(_.fold(f))
  //       f(DescriptorCase.ReferenceCase(c.name, typeArgs, c.attributes))
  //     case c @ DescriptorCase.FunctionCase(_, _, _, _) =>
  //       val args       = c.args.map(_.fold(f))
  //       val returnType = c.returnType.fold(f)
  //       f(DescriptorCase.FunctionCase(c.name, args, returnType, c.attributes))
  //   }
  // }
  object Descriptor {
    def apply[Attribs](caseValue: DescriptorCase[Attribs, Descriptor[Attribs]]): Descriptor[Attribs]    = ???
    def variable[Attribs](caseValue: DescriptorCase[Attribs, Descriptor[Attribs]]): Descriptor[Attribs] = ???

    sealed trait DescriptorCase[+Attribs, +A] { self =>
      import DescriptorCase.*

      def map[B](f: A => B): DescriptorCase[Attribs, B] = self match {
        case c @ VariableCase(_, _)     => c
        case c @ ReferenceCase(_, _, _) => ReferenceCase(c.name, c.typeVars.map(f), c.attributes)
        case c @ FunctionCase(_, _, _, _) =>
          FunctionCase(c.name, c.args.map(f), f(c.returnType), c.attributes)
      }
    }

    object DescriptorCase {
      // def typeDescriptor(name: String): DescriptorCase[Nothing] = ???
      final case class VariableCase[+Attribs](name: String, attributes: Attribs)
          extends DescriptorCase[Attribs, Nothing]
      final case class ReferenceCase[+Attribs, +A](name: String, typeVars: List[A], attributes: Attribs)
          extends DescriptorCase[Attribs, A]
      final case class FunctionCase[+Attribs, +A](
          name: Option[String],
          args: List[A],
          returnType: A,
          attributes: Attribs
      ) extends DescriptorCase[Attribs, A]
    }
  }

}
