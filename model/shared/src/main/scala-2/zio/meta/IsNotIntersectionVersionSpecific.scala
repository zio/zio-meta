package zio.meta

trait IsNotIntersectionVersionSpecific {
  implicit def materialize[A]: IsNotIntersection[A] =
    macro zio.meta.internal.macros.InternalMacros.materializeIsNotIntersection[A]
}
