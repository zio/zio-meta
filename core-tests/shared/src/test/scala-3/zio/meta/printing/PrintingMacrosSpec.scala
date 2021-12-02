package zio.meta.printing

import zio.test.*

object PrintingMacrosSpec extends DefaultRunnableSpec:
  def spec = suite("PrintingMacros Spec")(
    test("dump should return the passed in value"){
      inline val a = 1
      inline val b = 2
      val c = 3
      val actual = PrintingMacros.dump(a + b + c)
      assertTrue(actual == 6)
    }
  )