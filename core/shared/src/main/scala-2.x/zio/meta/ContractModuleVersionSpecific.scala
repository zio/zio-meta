package zio.meta

trait ContractModuleVersionSpecific {}

trait ServiceContractCompanionVersionSpecific {}

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
