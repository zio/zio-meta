package zio.meta
import zio.test.*
object TypeMonikerMacrosSpec extends zio.test.DefaultRunnableSpec:
  def spec = suite("MonikerMacros Spec")(
    test("It should support getting the moniker for a kind 0 case class"){
      val moniker = TypeMonikerMacros.moniker[Person]
      printing.PrintingMacros.dumpTypeTree[Person]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Person")
    } + test("It should support getting the moniker for a parametic trait"){
      val moniker = TypeMonikerMacros.moniker[Func[Int,String]]
      printing.PrintingMacros.dumpTypeTree[Func[Int,String]]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Func[scala.Int, java.lang.String]")
    }+ test("It should support getting the moniker for a parametic trait"){
      //TODO: See if we can make this print a wildcard symbol instead of scala.Nothing
      val moniker = TypeMonikerMacros.moniker[Func[Int,?]]
      printing.PrintingMacros.dumpTypeTree[Func[?,?]]
      //zio.meta.TypeMonikerMacrosSpec.Func[scala.Int, Result]
      assertTrue(moniker != null) && assertTrue(moniker.name == "zio.meta.TypeMonikerMacrosSpec$.Func[scala.Int, scala.Nothing]")
    }
  )

  case class Person(name: String, age: Int)



  trait Func[Params,Result] {
    def func1(params: Params): Result
    def func2(a:1, b:2): Result
  }

  /// TypeDescriptor(name= Func, args=List(TypeVar("Params"), TypeVar("Result"))), members = List(Function(name=func1, args=)))
end TypeMonikerMacrosSpec
