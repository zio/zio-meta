package zio.meta
import zio.test.*

object NamingSpec extends DefaultRunnableSpec {
  import metaverse.*

  def spec = suite("NamingSpec")(
    suite("When kind is Text")(
      test("Should return the canonical value when calling toString") {
        val sut = Name("Thingy")
        assertTrue(sut.toString == sut.canonical) && assertTrue(sut.canonical == "Thingy")
      }
    ),
    suite("When kind is Synthetic")(
      test("Should return the canonical value when calling toString") {
        val sut = Name("Thingy").toSynthetic(1)
        assertTrue(sut.toString == sut.canonical) && assertTrue(sut.canonical == "Thingy$1")
      }
    )
  )
}
