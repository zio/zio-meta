package zio

import zio.meta.VersionSpecific

package object meta extends VersionSpecific {
  trait IsNotIntersection[A] extends Serializable

  object IsNotIntersection extends IsNotIntersectionVersionSpecific {
    def apply[A](implicit isNotIntersection: IsNotIntersection[A]): IsNotIntersection[A] = isNotIntersection
  }

}
