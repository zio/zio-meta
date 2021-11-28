package zio.meta

object facade extends zio.meta.FacadeVersionSpecific {
  sealed trait Facade {

    /** the type being mirrored */
    type MirroredType

    /** The mirrored *-type */
    type MirroredMonoType

    /** The name of the type */
    type MirroredLabel <: String
  }

  object Facade {
    type Of[T] = Facade { type MirroredType = T }
  }

  sealed trait ADTFacade extends Facade {

    /** the type of the elements of the mirrored type */
    type MirroredElemTypes

    /** The names of the elements of the type */
    type MirroredElemLabels <: Labels

  }

  object ADTFacade extends ADTFacadeVersionSpecific {
    type Of[T] = ADTFacade { type MirroredType = T }

    trait Product extends ADTFacade {
      def fromProduct(p: scala.Product): MirroredMonoType
    }

    trait Sum extends ADTFacade {
      def ordinal(x: MirroredMonoType): Int
    }
  }

}
