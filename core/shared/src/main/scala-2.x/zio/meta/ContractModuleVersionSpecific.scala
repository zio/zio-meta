package zio.meta

trait ContractModuleVersionSpecific {}

trait ServiceContractCompanionVersionSpecific { self: ServiceContract.type =>
  def serviceContract[T]: ServiceContract = ServiceContract[T]
}

trait ServiceContractForCompanionVersionSpecific { self: ServiceContractFor.type =>
  implicit def serviceContractFor[T]: ServiceContractFor[T] = new ServiceContractFor[T] {
    def serviceContract: ServiceContract = ServiceContract.serviceContract[T]
  }
}

// trait ServiceContract {
//   type ContractType
//   // type ContractLabel <: String
//   // def contractLabel: ContractLabel
// }

// object ServiceContract {
//   trait Of[T] extends ServiceContract { type ContractType = T }
//   object Of extends ServiceContractOfCompanionVersionSpecific {
//     def apply[T]: Of[T] = new Of {}
//   }
// }
