package zio.meta
import izumi.reflect.Tag
import zio.meta.MetaTag.Default

sealed trait ZMetaTag[+Ctx, T] { self =>
  protected def context: ZContext[Ctx]
  def tag: Tag[T]
}

object MetaTag {
  def apply[T](implicit tag: Tag[T]): MetaTag[T] = {
    val context = ZContext.empty
    Default(tag, context)
  }

  final case class Default[+Ctx, T](tag: Tag[T], context: ZContext[Ctx]) extends ZMetaTag[Ctx, T]
}

trait Attribute[A, F[_]] {
  def value: F[A]
}

//TODO: Make ZMetaTag and ZMetaStructure be types that derive from this and build up a hierarchy
trait ZMeta[Ctx, T] {
  protected def context: ZContext[Ctx]
}

object ZMeta {
  def apply[T]: Meta[T] = new ZMeta[Any, T] {
    val context = ZContext.empty
  }

}

/*+:
 *      Module
 *       - Types
 *          - TypeA - (BuildStructure, Name,  A)
 *          - TypeB - - (BuildStructure,Name,  A)
 *       - Functions
 *       - Fields
 */

