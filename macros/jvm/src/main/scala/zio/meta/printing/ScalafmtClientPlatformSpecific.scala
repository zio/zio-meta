package zio.meta.printing

import org.scalafmt.cli.*
import scala.util.matching.Regex
import java.io.File
import metaconfig.*, Configured.*
import org.scalafmt.Formatted
import org.scalafmt.Scalafmt
import org.scalafmt.config.ScalafmtConfig
import org.scalafmt.config.{ScalafmtRunner => SRunner}
import org.scalafmt.util.LoggerOps.*

/**
 * Based on ScalaFmt210 from scalafmt cli
 */
trait ScalafmtClientPlatformSpecific {
  val oldConfig: Regex = "--".r
  // Not really needed, just so that the cache can be normally populated
  val filename = "Main.scala"

  def format(code: String): String = {
    val style = ScalafmtConfig.default
    Scalafmt.format(code, style, Set.empty, "<input>") match {
      case Formatted.Success(formattedCode) =>
        formattedCode
      case Formatted.Failure(e) =>
        println(
          s"""===== Failed to format the code ====
             |$code
             |---
             |Cause: ${e.getMessage}.
             |""".stripMargin
        )
        code
    }
  }
}
