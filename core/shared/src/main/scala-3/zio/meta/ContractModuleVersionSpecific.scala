package zio.meta
import scala.quoted.*

trait ContractModuleVersionSpecific { this: ContractModule => }

trait ServiceContractCompanionVersionSpecific:
   self: ServiceContract.type =>
   inline def serviceContract[T]: ServiceContract[Unit] = ${ ServiceContractMacros.getServiceContract[T] }

  // given derived[T:Type](using Quotes)(using Type[ServiceContract])(using Type[ServiceContract.Of[T]]): Expr[ServiceContract.Of[T]] =
  //   import quotes.reflect.*
  //   '{ServiceContract.Of[T]}

    //def foo[T](using Mirror.Of[T]):Mirror.Of[T] = ???
end ServiceContractCompanionVersionSpecific

trait ServiceContractForVersionSpecific:
  def serviceContract[T]: ServiceContract[Unit]
end ServiceContractForVersionSpecific


private object ServiceContractMacros:
  def getServiceContract[T](using Type[T], Quotes):Expr[ServiceContract[Unit]] =
    import quotes.reflect.*
    '{ServiceContract.apply[Unit](null)}
end ServiceContractMacros

