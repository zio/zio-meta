package zio.meta

trait ContractModule extends ContractModuleVersionSpecific {}

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

trait OperationContract[T]
trait DataContract[T]

object contract extends ContractModule

trait ServiceContract {
  type ContractType
  // type ContractLabel <: String
  // def contractLabel: ContractLabel
}

object ServiceContract extends ServiceContractCompanionVersionSpecific {
  def apply[T]: ServiceContract = new ServiceContract {
    type ContractType = T
  }

  // trait Of[T] extends ServiceContract { final type ContractType = T }
  // object Of  {

  //   import scala.quoted.*

  //   def apply[T]: Of[T] = new Of[T] {}
  //   given derived[T](using Type[T], Quotes): Expr[ServiceContract.Of[T]] =
  //     import quotes.reflect.*
  //     implicit val t:Type[T] = summon[Type[T]]
  //     //'{ServiceContract.Of[${t.Underlying}]}
  // }
}
