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

  inline def dumpTypeTree[T]:Unit = ${ dumpTypeTreeImpl[T] }
  def dumpTypeTreeImpl[T](using tpe: Type[T])(using Quotes): Expr[Unit] =
    import quotes.reflect._
    val tpr = TypeRepr.of[T]
    println("================== Show Type Tree =============================")
    println(Format(TypeTree.of[T].show))
    println("================== Show Owner =============================")
    println(Format(tpr.typeSymbol.maybeOwner.fullName))
    println("================== Show FullName =============================")
    println(Format(tpr.typeSymbol.fullName))
    println("================== Done =============================")

    tpr.typeSymbol.typeMembers match {
      case Nil =>
        println("No members")
      case members =>
        println("Members:")
        members.foreach { member =>
          println(Format(member.fullName) + " isTypeParam:" + member.isTypeParam)
        }
    }

    '{ () }
  end dumpTypeTreeImpl

  inline def dump[T](inline code: => T):T = ${dumpImpl[T]('code)}
  def dumpImpl[T](code: => Expr[T])(using Type[T])(using Quotes):Expr[T] =
    import quotes.reflect.*
    println(Format(Printer.TreeStructure.show(code.asTerm)))
    code
  end dumpImpl
}


