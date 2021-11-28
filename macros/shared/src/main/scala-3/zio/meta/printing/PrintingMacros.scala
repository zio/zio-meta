package zio.meta.printing
import scala.quoted._

object PrintingMacros {
  inline def printTree(inline any: Any): Unit = ${ printTreeImpl('any) }
  def printTreeImpl(expr: Expr[Any])(using Quotes): Expr[Unit] = {
    import quotes.reflect._
    println("================== The Short Version ================")
    println(Format(Printer.TreeShortCode.show(expr.asTerm.underlyingArgument)))
    println("================== The Long Version ================")
    println(Format(Printer.TreeStructure.show(expr.asTerm.underlyingArgument)))
    println("================== The Very Long Version (Not Reduced) ================")
    println(Format(Printer.TreeStructure.show(expr.asTerm)))
    '{ () }
  }
}


