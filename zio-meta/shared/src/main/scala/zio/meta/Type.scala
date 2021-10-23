package zio.meta

sealed trait Type[A]:
  self =>


object Type:
  final case class Apply[R, A]()

final case class TypeDescriptor[-R, +A](name:Name)
