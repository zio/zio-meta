package zio.meta

trait ContractModule extends ContractModuleVersionSpecific {
  trait ServiceContract[T]
  object ServiceContract {
    def derived[T]: ServiceContract[T] = new ServiceContract[T] {}
  }

  trait OperationContract[T]
  trait DataContract[T]
}
