package zio.meta.model
import naming.*

final case class TypeDescriptor[-R, +A](
    name: Name,
    packageName: Option[PackageName]
)
