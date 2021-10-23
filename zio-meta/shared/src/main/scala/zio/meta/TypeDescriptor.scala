package zio.meta
import zio.meta.naming.*

final case class TypeDescriptor[-R, +A](
  name:Name,
  packageName:Option[PackageName]
)

