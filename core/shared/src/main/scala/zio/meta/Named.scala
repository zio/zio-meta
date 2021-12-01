package zio.meta

import izumi.reflect.Tag

final case class Named[K <: String & Singleton, A](name: K, value: A)

object Named {

  def named[K <: String & Singleton, A](name: K)(value: A) = Named(name, value)
}
