package zio.meta

sealed trait Ctx[+LeafAttributes, +BranchAttributes]
object Ctx {
  case object Empty extends Ctx[Nothing, Nothing]
  final case class Present[+Attrib](underlying: ZContext[Attrib])
}
