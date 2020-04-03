lazy val infoSettings = Seq(
  organization := "io.github.mvillafuertem",
  description := "Scalcite is a library",
  homepage := Some(url(s"https://github.com/mvillafuertem/scalcite")),
  licenses := List("MIT" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "mvillafuertem",
      "Miguel Villafuerte",
      "mvillafuertem@email.com",
      url("https://github.com/mvillafuertem")
    )
  ),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/mvillafuertem/scalcite"),
      "scm:git@github.com:mvillafuertem/scalcite.git"
    )
  )
)

lazy val `scalcite` = (project in file("."))
  .aggregate(
    `scalcite-blower`,
    `scalcite-circe-blower`,
    `scalcite-circe-flattener`,
    `scalcite-core`,
    `scalcite-docs`,
    `scalcite-example-backend`,
    `scalcite-example-frontend`,
    `scalcite-flattener`,
  )
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(crossScalaVersions := Nil)
  .settings(commands ++= Commands.value)

lazy val `scalcite-core` = (project in file("scalcite-core"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noAssemblyTest)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-core`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower`)
  .dependsOn(`scalcite-flattener`)

lazy val `scalcite-example-backend` = (project in file("scalcite-example/backend"))
  // S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(AssemblySettings.value)
  .settings(BuildInfoSettings.value)
  .settings(DockerSettings.value)
  .settings(NativePackagerSettings.value)
  .settings(NexusSettings.value)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-example-backend`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)
  .dependsOn(`scalcite-circe-blower`)
  .dependsOn(`scalcite-circe-flattener`)
  .dependsOn(`scalcite-circe-table`)
  // P L U G I N S
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(DockerPlugin)
  .enablePlugins(GitVersioning)

lazy val `scalcite-example-frontend` = (project in file("scalcite-example/frontend"))
  // S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(scalacOptions += "-Ymacro-annotations")
  .settings(Compile / npmDependencies += "react" -> "16.13.0")
  .settings(Compile / npmDependencies += "react-dom" -> "16.13.0")
  .settings(Compile / npmDependencies += "react-proxy" -> "1.1.8")
  .settings(Compile / npmDevDependencies += "copy-webpack-plugin" -> "5.1.1")
  .settings(Compile / npmDevDependencies += "css-loader" -> "3.4.2")
  .settings(Compile / npmDevDependencies += "file-loader" -> "5.1.0")
  .settings(Compile / npmDevDependencies += "html-webpack-plugin" -> "3.2.0")
  .settings(Compile / npmDevDependencies += "style-loader" -> "1.1.3")
  .settings(Compile / npmDevDependencies += "webpack-merge" -> "4.2.2")
  .settings(fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly())
  .settings(fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js"))
  .settings(fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"))
  .settings(fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js"))
  .settings(libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.4+2-3c8aef65")
  .settings(libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.6.4+2-3c8aef65")
  .settings(startWebpackDevServer / version  := "3.10.3")
  .settings(Test / requireJsDomEnv := true)
  .settings(Test / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-core.config.js"))
  .settings(webpack / version := "4.41.6")
  .settings(webpackResources := baseDirectory.value / "webpack" * "*")
  // P L U G I N S
  .enablePlugins(ScalaJSBundlerPlugin)

lazy val `scalcite-docs` = (project in file("scalcite-docs"))
  .dependsOn(`scalcite-example-backend` % "compile->test")
  // S E T T I N G S
  .settings(scalaSource in Compile := baseDirectory.value / "src/main/mdoc")
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(MdocSettings.value)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-docs`)
  // P L U G I N S
  .enablePlugins(MdocPlugin)

lazy val `scalcite-blower` = (project in file("scalcite-blower"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-blower`)

lazy val `scalcite-circe-blower` = (project in file("scalcite-circe/blower"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-blower`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower`)

lazy val `scalcite-flattener` = (project in file("scalcite-flattener"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-flattener`)

lazy val `scalcite-circe-flattener` = (project in file("scalcite-circe/flattener"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-flattener`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-flattener`)

lazy val `scalcite-circe-table` = (project in file("scalcite-circe/table"))
// S E T T I N G S
  .settings(infoSettings)
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-table`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)
