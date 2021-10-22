package zio.meta

import zio.prelude.*

trait Naming {

  object NamespaceName extends Newtype[String]
  type NamespaceName = NamespaceName.Type

}
