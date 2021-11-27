package zio.meta
import scala.quoted.*
import izumi.reflect.Tag
import zio.meta.internals.*

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
    println(s"T: ${tpr.show}")
    val args = tpr match {
      case a:AppliedType => a.args
      case _ => Nil
    }


    val typeParams = args.map {
      //TODO: Correct this behavior
      case tp @ TypeBounds(lo, hi) =>
        println(s"TypeBounds: ${lo.show}, ${hi.show}")
        lo
      case tp => println(s"TypeParam: ${tp.show}")
        tp
    }

    println(s"typeParams: ${typeParams.map(_.show).mkString(", ")}")

    val selfMoniker = getMonikerFor[T]
    val params =
      Expr.ofList(typeParams.map(getMonikerShim))

    '{ Moniker($selfMoniker, $params) }
  }

  private def getMonikerFor[T](using Type[T], Quotes): Expr[MonikerFor[T]] = {
    import quotes.reflect._

    val tag = Expr.summon[Tag[T]] match {
      case Some(ct) => ct
      case None =>
        report.error(
          s"Unable to find a Tag for type ${Type.show[T]}",
          Position.ofMacroExpansion
        )
        throw new Exception("Error while applying macro")
    }

    '{MonikerFor.fromTag($tag)}
  }
}