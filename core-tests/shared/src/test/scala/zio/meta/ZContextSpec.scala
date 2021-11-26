package zio.meta
import zio.test.*

object ZContextSpec extends DefaultRunnableSpec {
  def spec = suite("ZContextSpec")(
    suite("ZContext")(
      test("Should support constucting and getting metadata") {
        val metadata = ZContext("Hello")
        assertTrue(metadata.get == "Hello")
      } +
        test("Should support constucting and getting metadata with multiple values") {
          case class ApiKey(value: String)
          case class SshKey(value: String)

          val metadata = ZContext(ApiKey("a-b-c-d"), SshKey("ABCDEFG123"))
          assertTrue(metadata.get[ApiKey] == ApiKey("a-b-c-d"))
          assertTrue(metadata.get[SshKey] == SshKey("ABCDEFG123"))
        } +
        test("Should support overriding metadata") {
          case class ApiKey(value: String)
          case class SshKey(value: String)

          val metadata = ZContext(ApiKey("a-b-c-d"), SshKey("ABCDEFG123")) ++ ZContext(
            ApiKey("a-b-c-d-e"),
            SshKey("ABCDEFG123-456")
          )

          assertTrue(metadata.get[ApiKey] == ApiKey("a-b-c-d-e"))
          assertTrue(metadata.get[SshKey] == SshKey("ABCDEFG123-456"))
        }
    )
  )
}
