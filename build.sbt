import Dependencies.Version
import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage     := Some(url("https://zio.dev/zio-meta/")),
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
    examplesJVM,
    exampleJS,
    macrosJVM,
    macrosJS,
    macrosNative
  )

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("core"))
  .settings(stdSettings("zio-meta", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "izumi-reflect" % Version.`izumi-reflect`,
      "dev.zio" %%% "zio"           % Version.zio % Test
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
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val coreJVM = core.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
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
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
  .configure(_.enablePlugins(JCStressPlugin))
  .settings(replSettings)

lazy val coreTestsJS = coreTests.js
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
  .settings(dottySettings)

lazy val examples = crossProject(JSPlatform, JVMPlatform)
  .in(file("examples"))
  .dependsOn(macros, core)
  .settings(stdSettings("examples", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .settings(buildInfoSettings("zio.meta.examples"))
  .settings(publish / skip := true)
  //   Compile / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
  // )
  .enablePlugins(BuildInfoPlugin)

lazy val examplesJVM = examples.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
  .settings(replSettings)

lazy val exampleJS = examples.js
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % Version.zio % Test)
  .settings(dottySettings)

lazy val macros = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("macros"))
  .settings(stdSettings("zio-meta-macros", Scala3x, Scala213))
  .settings(crossProjectSettings)
  .settings(macroDefinitionSettings)
  .settings(macroExpansionSettings)

lazy val macrosJVM = macros.jvm
  .settings(dottySettings)
  .settings(
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "org.scalameta" %% "scalafmt-cli" % Version.scalafmt
          )
        case Some((3, _)) =>
          Seq(
            ("org.scalameta" %% "scalafmt-cli" % Version.scalafmt).cross(CrossVersion.for3Use2_13)
          )
        case _ =>
          Seq()
      }
    }
  )
lazy val macrosJS     = macros.js.settings(dottySettings)
lazy val macrosNative = macros.native.settings(nativeSettings)

lazy val docs = project
  .in(file("zio-meta-docs"))
  .settings(
    publish / skip := true,
    moduleName     := "zio-meta-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    projectName := "ZIO Meta",
    mainModuleName := (coreJVM / moduleName).value,
    projectStage := ProjectStage.Experimental,
    docsPublishBranch := "main"
  )
  .enablePlugins(WebsitePlugin)
