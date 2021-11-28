package zio.meta
import zio.meta.facade.*

trait FacadeVersionSpecific:
  final type Labels = Tuple
end FacadeVersionSpecific

trait ADTFacadeVersionSpecific:
  self: ADTFacade.type =>
  import scala.deriving.Mirror

  type FacadeForMirror = Mirror match
    case Mirror.Product => ADTFacade.Product
    case Mirror.Sum     => ADTFacade.Sum

  given facadeFromMirror[T](using mirror: Mirror.Of[T]): ADTFacade.Of[T] = ???
end ADTFacadeVersionSpecific