package zio.meta.internals

import scala.annotation.tailrec
import scala.deriving._
import scala.quoted._

private[meta] class ReflectionUtils[Q <: Quotes & Singleton](val q: Q):
  private given q.type = q
  import q.reflect.*

  def low(tp: TypeRepr): TypeRepr = tp match {
    case tp: TypeBounds => tp.low
    case tp => tp
  }

  def findMemberType(tp: TypeRepr, name: String): Option[TypeRepr] = tp match {
    case Refinement(_, `name`, tp) => Some(low(tp))
    case Refinement(parent, _, _) => findMemberType(parent, name)
    case AndType(left, right) => findMemberType(left, name).orElse(findMemberType(right, name))
    case _ => None
  }

end ReflectionUtils