val scala213             = "2.13.6"
val scala3x              = "3.1.0"
val primaryScalaVersion  = scala3x
val defaultScalaVersions = Seq(scala213, scala3x)

val Versions = new {
  val zioSchema = "1.0.0-RC6"
}

Global / excludeLintKeys := Set(logManager)
resolvers += Resolver.sonatypeRepo("snapshots")
inThisBuild(
  Seq(
    organization := "zio.dev",
    scalaVersion := primaryScalaVersion,
    version := "0.1.0-SNAPSHOT"
  )
)

val baseScalacSettings =
  "-encoding" :: "UTF-8" ::
    "-unchecked" ::
    "-deprecation" ::
    "-explaintypes" ::
    "-feature" ::
    "-language:_" ::
    "-Xfuture" ::
    "-Xlint" ::
    "-Ymacro-annotations" ::
    "-Yno-adapted-args" ::
    "-Ywarn-value-discard" ::
    "-Ywarn-unused" ::
    "-Xsource:3" ::
    Nil

lazy val scalacSettings = Seq(
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) =>
        baseScalacSettings.diff(
          "-Xfuture" ::
            "-Yno-adapted-args" ::
            "-Ywarn-infer-any" ::
            "-Ywarn-nullary-override" ::
            "-Ywarn-nullary-unit" ::
            Nil
        )
      case Some((3, _)) =>
        baseScalacSettings.diff(
          "-explaintypes" ::
            "-Xfuture" ::
            "-Xlint" ::
            "-Xsource:3" ::
            "-Ymacro-annotations" ::
            "-Yno-adapted-args" ::
            "-Ywarn-value-discard" ::
            "-Ywarn-unused" ::
            Nil
        )
      case _ => baseScalacSettings
    }
  }
)

val commonLoggingSettings = {
  import sbt.internal.LogManager
  import sbt.internal.util._
  import java.io.PrintStream

  Seq(logManager := {
    val printStream: PrintStream = new PrintStream(System.out) {
      val proj    = thisProject.value
      val project = s"${proj.id}"

      override def println(str: String): Unit = {
        val (lvl, msg) = str.span(_ != ']')
        super.println(s"$lvl] [$project$msg]")
      }
    }
    LogManager.defaultManager(ConsoleOut.printStreamOut(printStream))

  })
}

lazy val commonSettings = commonLoggingSettings ++ scalacSettings

lazy val zioMeta = (projectMatrix in file("zio-meta"))
  .settings(
    name := "zio-meta"
  )
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio-prelude" % Versions.zioSchema
    )
  )
  .jvmPlatform(scalaVersions = defaultScalaVersions)
  .jsPlatform(scalaVersions = defaultScalaVersions)
