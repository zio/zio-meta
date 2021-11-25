package zio.meta

import zio.meta.naming.*
import zio.meta.testing.*

import zio.test.*
import zio.test.Assertion.*

object NamingSpec extends ZioMetaBaseSpec {
  def spec = suite("Naming Spec")(
    suite("PackageName")(
      test("it should") {
        val sut = PackageName("foo.bar.baz")
        assertTrue(sut.fullName == "foo.bar.baz")
      })
    )
}