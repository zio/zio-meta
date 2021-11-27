package zio.meta

import scala.quoted.*

trait ServiceContractForCompanionVersionSpecific:
  self: ServiceContractFor.type =>

  given serviceContractFor[T]: ServiceContractFor[T] =
    new ServiceContractFor[T] {
      inline override def serviceContract: ServiceContract = ServiceContract.serviceContract[T]
    }

end ServiceContractForCompanionVersionSpecific
