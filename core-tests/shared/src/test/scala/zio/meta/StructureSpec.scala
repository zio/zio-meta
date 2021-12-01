package zio.meta

import zio.test.*

object StructureSpec extends DefaultRunnableSpec {
  def spec = suite("Structure Spec")(
    test("Should be able to get the structure of a parameterized trait") {
      val actual = Structure.of[ParameterizedTestModule[?, ?]]
      println(s"Structure: $actual")
      actual match {
        case sut @ Structure.TypeStructure(name, fullName, typeParameters) =>
          assertTrue(name == "ParameterizedTestModule[Model,Context]") && assertTrue(
            fullName == "zio.meta.StructureSpec$.ParameterizedTestModule[Model,Context]"
          ) && assertTrue(typeParameters.size == 2) && assertTrue(
            sut.show == "ParameterizedTestModule[Model, Context]"
          )
        case _ => ??? // fail("Expected a TypeStructure")
      }
    }
  )

  trait ParameterizedTestModule[Model, Context] {
    def getModel(context: Context): Model
  }
}
