package zio.meta.printing

trait ScalafmtClientPlatformSpecific {
  def format(code: String): String = code
}
