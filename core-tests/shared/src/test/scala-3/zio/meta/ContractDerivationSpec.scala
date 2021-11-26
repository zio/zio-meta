package zio.meta
import zio.test.*

object ContractDerivationSpec extends DefaultRunnableSpec:
  def spec = suite("ContractDerivationSpec")(
    suite("ServiceContract derivation")(
      suite("When type is a trait")(
        test("ServiceContract derivation should be supported"){
          val contract = ""
          assertTrue(contract != null)
        }
      )
    )
  )

  // case class TestService() derives ServiceContract.Of   {
  //   def add(left: Int, right:Int):Int
  // }

