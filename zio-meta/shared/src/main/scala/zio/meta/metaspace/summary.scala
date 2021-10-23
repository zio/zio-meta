package zio.meta.metaspace
import zio.meta.*
import zio.meta.naming.*

object summary extends zio.meta.Metaspace(using SummarySupport.summaryTypeModule):
  final case class TypeDescriptor[-R, +A](
    name:Name,
    packageName:Option[PackageName])

  import typed.*
  def typeOf[A]:Type[A] = ???



object SummarySupport:
  given summaryTypeModule:TypeModule = new TypeModule{}

  given (using typed:TypeModule):typed.Type[Unit] =
    val descriptor:TypeDescriptor[Any,Unit] = ???
    //Type.Apply(descriptor, None)
    ???

object summaryExample:
  summary.typeOf[Unit]