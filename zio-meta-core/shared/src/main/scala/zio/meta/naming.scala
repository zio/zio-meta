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

  enum PackageName:
    case Root
    case Child(name:String)

  object PackageName:
    def apply(name:String):PackageName =
      name.split("\\.").toList match
        case Nil => Root
        case head::tail => Child(tail.foldLeft(head)(_ + "." + _))