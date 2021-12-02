package zio.meta.printing

object PrintingMacrosUsage:
  def main(args: Array[String]): Unit =
    inline def foo = "blah"
    inline def bar = foo
    val v = PrintingMacros.printTree(bar)
    println(s"Value(v): $v") //
    println("================== Type Information ================")
    inline def printIt = PrintingMacros.dumpTypeTree[String]
    printIt