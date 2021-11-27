package zio.meta
import scala.quoted.*
import scala.reflect.ClassTag

object TypeMonikerMacros {
  import TypeMoniker.Moniker

  inline def moniker[T]:Moniker = ${getMoniker[T]}

  private def getMoniker[T](using Type[T], Quotes):Expr[Moniker] = {
    import quotes.reflect._

    def getMonikerShim(tpr:TypeRepr)(using Quotes):Expr[Moniker] =
      tpr.asType match {
        case '[t] => getMoniker[t]
      }

    val tpr = TypeRepr.of[T]
    val typeParams = tpr match {
      case a:AppliedType => a.args
      case _ => Nil
    }

    val selfMoniker = getMonikerFor[T]
    val params =
      Expr.ofList(typeParams.map(getMonikerShim))

    '{ Moniker($selfMoniker, $params) }
  }

  private def getMonikerFor[T](using Type[T], Quotes): Expr[MonikerFor[T]] = {
    import quotes.reflect._

    val classTag = Expr.summon[ClassTag[T]] match {
      case Some(ct) => ct
      case None =>
        report.error(
          s"Unable to find a ClassTag for type ${Type.show[T]}",
          Position.ofMacroExpansion
        )
        throw new Exception("Error while applying macro")
    }

    '{MonikerFor($classTag)}
  }
}