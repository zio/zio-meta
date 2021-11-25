package zio.meta

trait NamingModule {
  import Name.NameKind

  final case class Name(text: String, kind: NameKind[Name]) { self =>
    lazy val canonical: String = fold[String] {
      case NameKind.Text                         => text
      case NameKind.Synthetic(seqNo, underlying) => underlying + "$" + seqNo
    }

    def fold[Z](f: NameKind[Z] => Z): Z =
      kind match {
        case k @ NameKind.Text                         => f(k.asInstanceOf[NameKind[Z]])
        case k @ NameKind.Synthetic(seqNo, underlying) => f(NameKind.Synthetic(seqNo, underlying.fold(f)))
      }

    def toSynthetic(seqNo: Int): Name = transform {
      case NameKind.Text                => NameKind.Synthetic(seqNo, self)
      case k @ NameKind.Synthetic(_, _) => k
    }

    override def toString: String = canonical

    def transform(f: NameKind[Name] => NameKind[Name]): Name =
      kind match {
        case k @ NameKind.Text                         => Name(text, f(k.asInstanceOf[NameKind[Name]]))
        case k @ NameKind.Synthetic(seqNo, underlying) => Name(text, f(k.asInstanceOf[NameKind[Name]]))
      }
  }

  object Name {
    def apply(input: String): Name = text(input)
    def text(input: String): Name  = new Name(input, NameKind.Text)

    sealed trait NameKind[+A] { self =>
      def map[B](f: A => B): NameKind[B] = self match {
        case NameKind.Text                         => NameKind.Text
        case NameKind.Synthetic(seqNo, underlying) => NameKind.Synthetic(seqNo, f(underlying))
      }
    }

    object NameKind {
      case object Text                                             extends NameKind[Nothing]
      case class Synthetic[+A](sequenceNumber: Int, underlying: A) extends NameKind[A]
    }
  }
}
