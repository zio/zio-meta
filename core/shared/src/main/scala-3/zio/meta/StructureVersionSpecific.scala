package zio.meta
import scala.quoted.*
import zio.meta.Structure.*

trait StructureVersionSpecific:
  inline def of[T]:Structure[T] = ${StructureMacros.structureOf[T]}

object StructureMacros:
  def structureOf[T](using Type[T])(using Quotes):Expr[Structure[T]] =
    import quotes.reflect.*
    val tpr = TypeRepr.of[T]
    val typeArgs: List[TypeRepr] = tpr.typeSymbol.typeMembers.map(typeSymbol => tpr.memberType(typeSymbol))
    val typeMembers = tpr.classSymbol.get.typeMembers.map(m => (m, tpr.memberType(m)))

    val name = Expr(tpr.typeSymbol.name)
    val typeVars = Expr.ofList(tpr.typeSymbol.typeMembers.map { typeMember =>
      val name = Expr(typeMember.name)
      '{TypeVar($name)}
    })
    val fullName = Expr(tpr.typeSymbol.fullName)
    '{TypeStructure[T]($name, $fullName, $typeVars)}
  end structureOf