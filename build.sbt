import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage     := Some(url("https://zio.github.io/zio-meta/")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "jdegoes",
        "John De Goes",
        "john@degoes.net",
        url("http://degoes.net")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc")
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  Seq(
    "internalMacrosJVM/test",
    "modelJVM/test"
  ).mkString(";")
)
addCommandAlias(
  "testJS",
  Seq(
    "internalMacrosJS/test",
    "modelJS/test"
  ).mkString(";")
)
addCommandAlias(
  "testNative",
  ";internalMacrosNative/test:compile"
)

val zioVersion          = "1.0.12"
val izumiReflectVersion = "2.0.8"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    modelJVM,
    modelJS,
    internalMacrosJVM,
    internalMacrosJS,
    internalMacrosNative
    // docs
  )

lazy val internalMacros = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("internal-macros"))
  .settings(stdSettings("zio-meta-internal-macros", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(macroDefinitionSettings)
  .settings(macroExpansionSettings)

lazy val internalMacrosJVM    = internalMacros.jvm.settings(dottySettings)
lazy val internalMacrosJS     = internalMacros.js.settings(dottySettings)
lazy val internalMacrosNative = internalMacros.native.settings(nativeSettings)

lazy val model = crossProject(JSPlatform, JVMPlatform)
  .in(file("model"))
  .settings(stdSettings("zio-meta-model", Scala3x))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta.model"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "izumi-reflect" % izumiReflectVersion,
      "dev.zio" %%% "zio"           % zioVersion % Test
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(internalMacros)

lazy val modelJS = model.js
  .settings(jsSettings)
  // .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val modelJVM = model.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

// lazy val docs = project
//   .in(file("zio-meta-docs"))
//   .settings(stdSettings("zio-meta"))
//   .settings(
//     publish / skip := true,
//     moduleName := "zio-meta-docs",
//     scalacOptions -= "-Yno-imports",
//     scalacOptions -= "-Xfatal-warnings",
//     ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(modelJVM),
//     ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
//     cleanFiles += (ScalaUnidoc / unidoc / target).value,
//     docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
//     docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
//   )
//   .dependsOn(modelJVM)
//   .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
