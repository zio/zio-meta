package zio.meta

object naming:
  sealed trait Name

  // enum Name:
  //   case SynthesizedTypeName(owner:Name,suffix:String)
  //   case TypeName(name:String)
  //   case QualifiedName(packageName)

  object Name:
    def fromString(input:String):Name = TypeName(input)


  enum TypeName extends Name:
    self =>
    case Local(name:String)
    case Qualified(packageName:PackageName,name:Local)

    def localName:String = self match
      case Local(name) => name
      case Qualified(_,Local(name)) => name

  object TypeName:
    def apply(name:String):TypeName = Local(name)

  opaque type PackageNameSegment = String

  final case class PackageName(parts:List[PackageNameSegment]) { self =>
    def fullName:String = parts.mkString(".")
    override def toString:String = fullName
  }

  object PackageName:
    val Root:PackageName = PackageName(Nil)

    def apply(name:String):PackageName =
      PackageName(name.split("\\.").toList)