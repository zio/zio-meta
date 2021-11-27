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

object contract extends ContractModule {}

final case class ServiceContract[+Attribs](contractType: TypeDescriptor[Attribs])

object ServiceContract extends ServiceContractCompanionVersionSpecific {

  // trait Of[T] extends ServiceContract { final type ContractType = T }
  // object Of  {

  //   import scala.quoted.*

  //   def apply[T]: Of[T] = new Of[T] {}
  //   given derived[T](using Type[T], Quotes): Expr[ServiceContract.Of[T]] =
  //     import quotes.reflect.*
  //     implicit val t:Type[T] = summon[Type[T]]
  //     //'{ServiceContract.Of[${t.Underlying}]}
  // }

  final case class Descriptor[+Attribs](
      contractType: TypeDescriptor[Attribs]
  )

  trait TypeLevel {
    type ContractType
  }

}

trait ServiceContractFor[T] {
  def serviceContract: ServiceContract[Unit]
}

object ServiceContractFor extends ServiceContractForCompanionVersionSpecific {
  implicit def apply[T](implicit serviceContractFor: ServiceContractFor[T]): ServiceContractFor[T] =
    serviceContractFor
}

object example {
  val serviceContract1 = ServiceContract.Descriptor(
    contractType = TypeDescriptor.reference("zio.meta.TestService")
  )
}
