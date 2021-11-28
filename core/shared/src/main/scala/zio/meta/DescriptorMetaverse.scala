package zio.meta

trait DescriptorMetaverse {
  import Descriptor.DescriptorCase

  final case class Descriptor(caseValue: DescriptorCase[Descriptor]) { self =>
    def fold[Z](f: DescriptorCase[Z] => Z): Z = caseValue match {
      case c @ DescriptorCase.TypeDescriptor(_) => f(c)
    }

  }

  object Descriptor {
    sealed trait DescriptorCase[+A] { self =>
      import DescriptorCase.*

      def map[B](f: A => B): DescriptorCase[B] = self match {
        case TypeDescriptor(name) => TypeDescriptor(name)
      }
    }

    object DescriptorCase {
      final case class TypeDescriptor(name: String) extends DescriptorCase[Nothing]
    }
  }
}
