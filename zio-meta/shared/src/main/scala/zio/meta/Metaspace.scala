package zio.meta
import zio.meta.naming.*

abstract class Metaspace(using typeModule:TypeModule) {
  val typed:TypeModule = typeModule

  def typeOf[A]:typed.Type[A]
}

trait TypeModule:
  type TypeDescriptor[-R,A] <: TypeDescriptorApi[R,A]

  enum Type[A]:
    self =>

    def label:String = self match
      case Apply(td,_) => td.name.toString
      case Union(left, right) =>
        val leftLabel = left.label
        val rightLabel = right.label
        s"$leftLabel | $rightLabel"
      case Intersection(left, right) =>
        val leftLabel = left.label
        val rightLabel = right.label
        s"$leftLabel & $rightLabel"

    def |[B](that: Type[B]): Type[A | B] = Union(self,that)
    def &[B](that: Type[B]): Type[A & B] = Intersection(self,that)

    case Apply[R, A](typeDesc:TypeDescriptor[R,A], params:Option[Type[R]]) extends Type[A]
    case Union[A,B](left:Type[A], right:Type[B]) extends Type[A|B]
    case Intersection[A,B](left:Type[A], right:Type[B]) extends Type[A&B]


  trait TypeDescriptorApi[-R,A]:
    def name: TypeName.Qualified
    def typeParams: List[TypeName.Qualified]

end TypeModule


// Name - optional and may bot exist
// Rendering - a rendering of the name
// Id - stable