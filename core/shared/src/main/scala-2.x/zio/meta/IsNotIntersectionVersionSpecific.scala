package zio.meta
import zio.meta.internal.macros.InternalMacros

trait IsNotIntersectionVersionSpecific {
  implicit def materialize[A]: IsNotIntersection[A] =
    macro InternalMacros.materializeIsNotIntersection[A]
}
