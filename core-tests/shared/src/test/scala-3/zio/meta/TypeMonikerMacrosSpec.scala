package zio.meta
import zio.test.*
object TypeMonikerMacrosSpec extends zio.test.DefaultRunnableSpec:
  def spec = suite("MonikerMacros Spec")(
    test("It should support getting the moniker for a kind 0 case class"){
      val moniker = TypeMonikerMacros.moniker[Person]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$Person")
    }
  )

  case class Person(name: String, age: Int)
end TypeMonikerMacrosSpec