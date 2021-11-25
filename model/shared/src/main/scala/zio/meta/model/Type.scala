package zio.meta.model

enum Type[A]:
  self =>

  def |[B](that: Type[B]): Type[A | B] = Union(self,that)
  def &[B](that: Type[B]): Type[A & B] = Intersection(self,that)

  case Apply[R, A](typeDesc:TypeDescriptor[R,A], params:Option[Type[R]]) extends Type[A]
  case Union[A,B](left:Type[A], right:Type[B]) extends Type[A|B]
  case Intersection[A,B](left:Type[A], right:Type[B]) extends Type[A&B]

