package zio.meta

package object model extends VersionSpecific {
  trait IsNotIntersection[A] extends Serializable

  object IsNotIntersection extends IsNotIntersectionVersionSpecific {
    def apply[A](implicit isNotIntersection: IsNotIntersection[A]): IsNotIntersection[A] = isNotIntersection
  }

}
