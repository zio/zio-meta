package zio.meta.testing

import zio.duration.*
import zio.test.*

abstract class ZioMetaBaseSpec extends DefaultRunnableSpec {
  override def aspects = List(
    TestAspect.timeout(1.minute)
  )
}