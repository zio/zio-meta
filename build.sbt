import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.github.io/zio-meta/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
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
  ";zioMetaJVM/test"
)
addCommandAlias(
  "testJS",
  ";zioMetaJS/test"
)
addCommandAlias(
  "testNative",
  ";zioMetaNative/test:compile"
)

val sandwichSettings = Seq(
  scalacOptions
)

val zioVersion = "1.0.12"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    zioMetaCoreJVM,
    zioMetaCoreJS,
    zioMetaCompatJVM,
    zioMetaCompatJS,
    zioMetaJVM,
    zioMetaJS,
    docs
  )

lazy val zioMeta = crossProject(JSPlatform, JVMPlatform)
  .in(file("zio-meta"))
  .dependsOn(zioMetaCore)
  .settings(std3xSettings("zio-meta"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-test"     % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)

lazy val zioMetaJS = zioMeta.js
  .settings(jsSettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val zioMetaJVM = zioMeta.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

lazy val zioMetaCompat = crossProject(JSPlatform, JVMPlatform)
  .in(file("zio-meta-compat"))
  .dependsOn(zioMetaCore)
  .settings(stdSettings("zio-meta-compat"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta.compat"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-test"     % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    ),
    excludeDependencies ++= Seq(
      "dev.zio" % "zio_3",
      "dev.zio" % "zio-test_3",
      "dev.zio" % "zio-test-sbt_3"
    ),
    scalacOptions ++= Seq(
      "-Ytasty-reader"
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)

lazy val zioMetaCompatJS = zioMetaCompat.js
  .settings(jsSettings)
  .settings(
    libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test,
    excludeDependencies ++= Seq(
      "dev.zio" % "zio_3",
      "dev.zio" % "zio-test_3",
      "dev.zio" % "zio-test-sbt_3",
      "io.github.cquiroz" % "scala-java-time_sjs1_3",
      "io.github.cquiroz" % "scala-java-time-tzdb_sjs1_3"
    )
  )
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val zioMetaCompatJVM = zioMetaCompat.jvm
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

lazy val zioMetaCore = crossProject(JSPlatform, JVMPlatform)
  .in(file("zio-meta-core"))
  .settings(std3xSettings("zio-meta-core"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.meta.core"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-test"     % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)

lazy val zioMetaCoreJS = zioMetaCore.js
  .settings(jsSettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val zioMetaCoreJVM = zioMetaCore.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

lazy val docs = project
  .in(file("zio-meta-docs"))
  .settings(stdSettings("zio-meta"))
  .settings(
    publish / skip := true,
    moduleName := "zio-meta-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioMetaJVM),
    ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .dependsOn(zioMetaJVM)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
