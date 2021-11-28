package zio.meta
import zio.test.*

object TypeDescriptorSpec extends DefaultRunnableSpec {
  def spec = suite("TypeDescriptor Spec") {
    test("Should support mapping attributes") {
      val sut      = TypeDescriptor.reference("List", List(TypeDescriptor.variable("A")))
      val expected = TypeDescriptor.reference("List", List(TypeDescriptor.variable("A", 42)), 42)
      val actual   = sut.mapAttributes(_ => 42)
      assertTrue(actual == expected)
    }
  }
}
