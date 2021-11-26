package zio.meta

import zio.test.*

object ServiceContractSpec extends zio.test.DefaultRunnableSpec {
  def spec = suite("ServiceContractMacros Spec")(
    test("It should be possible to get the ServiceContract for a kind 0 object") {
      val actual = ServiceContract.serviceContract[TestService]
      assertTrue(actual != null)
    }
  )

  type TestService = TestService.type
  object TestService {
    def add(left: Int, right: Int): Int = left + right
  }
}
