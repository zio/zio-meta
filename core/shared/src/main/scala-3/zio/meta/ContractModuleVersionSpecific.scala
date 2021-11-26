package zio.meta



trait ContractModuleVersionSpecific { this: ContractModule => }

trait ServiceContractCompanionVersionSpecific:
    inline def serviceContract[T]: ServiceContract = ${ ServiceContractMacros.getServiceContract[T] }

  // given derived[T:Type](using Quotes)(using Type[ServiceContract])(using Type[ServiceContract.Of[T]]): Expr[ServiceContract.Of[T]] =
  //   import quotes.reflect.*
  //   '{ServiceContract.Of[T]}

    //def foo[T](using Mirror.Of[T]):Mirror.Of[T] = ???
end ServiceContractCompanionVersionSpecific


import scala.quoted.*
private object ServiceContractMacros {
  def getServiceContract[T](using Type[T], Quotes):Expr[ServiceContract] =
    import quotes.reflect.*
    '{ServiceContract.apply[T]}
}
