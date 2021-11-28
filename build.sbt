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
    "macrosJVM/test",
    "coreJVM/test",
    "coreTestsJVM/test"
  ).mkString(";")
)
addCommandAlias(
  "testJS",
  Seq(
    "macrosJS/test",
    "coreJS/test",
    "coreTestsJS/test"
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
    coreJVM,
    coreJS,
    coreTestsJVM,
    coreTestsJS,
    macrosJVM,
    macrosJS,
    macrosNative
    // docs
  )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("core"))
  .settings(stdSettings("zio-meta", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "izumi-reflect" % izumiReflectVersion,
      "dev.zio" %%% "zio"           % zioVersion % Test
    )
  )
  .settings(macroDefinitionSettings)
  // .settings(
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(macros)

lazy val coreJS = core.js
  .settings(jsSettings)
  // .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val coreJVM = core.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

lazy val coreTests = crossProject(JSPlatform, JVMPlatform)
  .in(file("core-tests"))
  .dependsOn(macros, core)
  .settings(stdSettings("core-tests", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .settings(buildInfoSettings("zio.meta.tests"))
  .settings(publish / skip := true)
  //   Compile / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
  // )
  .enablePlugins(BuildInfoPlugin)

lazy val coreTestsJVM = coreTests.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .configure(_.enablePlugins(JCStressPlugin))
  .settings(replSettings)

lazy val coreTestsJS = coreTests.js
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(dottySettings)

lazy val macros = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("macros"))
  .settings(stdSettings("zio-meta-macros", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(macroDefinitionSettings)
  .settings(macroExpansionSettings)

lazy val macrosJVM    = macros.jvm.settings(dottySettings)
lazy val macrosJS     = macros.js.settings(dottySettings)
lazy val macrosNative = macros.native.settings(nativeSettings)

// lazy val docs = project
//   .in(file("zio-meta-docs"))
//   .settings(stdSettings("zio-meta"))
//   .settings(
//     publish / skip := true,
//     moduleName := "zio-meta-docs",
//     scalacOptions -= "-Yno-imports",
//     scalacOptions -= "-Xfatal-warnings",
//     ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(coreJVM),
//     ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
//     cleanFiles += (ScalaUnidoc / unidoc / target).value,
//     docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
//     docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
//   )
//   .dependsOn(coreJVM)
//   .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
