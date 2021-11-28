package zio.meta
import zio.test.*
object TypeMonikerMacrosSpec extends zio.test.DefaultRunnableSpec:
  def spec = suite("MonikerMacros Spec")(
    test("It should support getting the moniker for a kind 0 case class"){
      val moniker = TypeMonikerMacros.moniker[Person]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Person")
    } + test("It should support getting the moniker for a parametic trait"){
      val moniker = TypeMonikerMacros.moniker[Func[Int,String]]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Func[scala.Int, java.lang.String]")
    }+ test("It should support getting the moniker for a parametic trait"){
      //TODO: See if we can make this print a wildcard symbol instead of scala.Nothing
      val moniker = TypeMonikerMacros.moniker[Func[Int,?]]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Func[scala.Int, scala.Nothing]")
    }
  )

  case class Person(name: String, age: Int)
  trait Func[Params,Result]
end TypeMonikerMacrosSpec