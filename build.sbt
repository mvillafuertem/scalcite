Global / onLoad := {
  val GREEN = "\u001b[32m"
  val RESET = "\u001b[0m"
  println(s"""$GREEN
             |$GREEN        ███████╗  ██████╗  █████╗  ██╗       ██████╗ ██╗ ████████╗ ███████╗
             |$GREEN        ██╔════╝ ██╔════╝ ██╔══██╗ ██║      ██╔════╝ ██║ ╚══██╔══╝ ██╔════╝
             |$GREEN        ███████╗ ██║      ███████║ ██║      ██║      ██║    ██║    █████╗
             |$GREEN        ╚════██║ ██║      ██╔══██║ ██║      ██║      ██║    ██║    ██╔══╝
             |$GREEN        ███████║ ╚██████╗ ██║  ██║ ███████╗ ╚██████╗ ██║    ██║    ███████╗
             |$GREEN        ╚══════╝  ╚═════╝ ╚═╝  ╚═╝ ╚══════╝  ╚═════╝ ╚═╝    ╚═╝    ╚══════╝
             |$RESET        v.${version.value}
             |""".stripMargin)
  (Global / onLoad).value
}

lazy val configurationPublish: Project => Project =
  _.settings(Information.value)
    .settings(Settings.value)
    .settings(crossScalaVersions := Settings.supportedScalaVersions)

lazy val configurationNoPublish: Project => Project =
  _.settings(Information.value)
    .settings(Settings.value)
    .settings(Settings.noPublish)

lazy val `scalcite` = (project in file("."))
  .configure(configurationNoPublish)
  .aggregate(
    `scalcite-blower`,
    `scalcite-circe-blower`,
    `scalcite-circe-flattener`,
    `scalcite-console`,
    `scalcite-core`,
    `scalcite-docs`,
    `scalcite-flattener`,
    `scalcite-server`,
    `scalcite-ui`
  )
  .settings(commands ++= Commands.value)

lazy val `scalcite-core` = (project in file("modules/scalcite-core"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-core`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower`)
  .dependsOn(`scalcite-flattener`)

lazy val `scalcite-server` = (project in file("modules/scalcite-server"))
  .configure(configurationNoPublish)
  // S E T T I N G S
  .settings(AssemblySettings.value)
  .settings(BuildInfoSettings.value)
  .settings(DockerSettings.value)
  .settings(NativePackagerSettings.value)
  .settings(NexusSettings.value)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-server`)
  .settings(javaAgents += JavaAgent(Dependencies.elasticApm))
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)
  .dependsOn(`scalcite-circe-blower`)
  .dependsOn(`scalcite-circe-flattener`)
  .dependsOn(`scalcite-circe-table`)
  // P L U G I N S
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAgent)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(GitVersioning)

lazy val `scalcite-console` = (project in file("modules/scalcite-console"))
  .configure(configurationNoPublish)
  // S E T T I N G S
  .settings(BuildInfoSettings.value)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-console`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)
  .dependsOn(`scalcite-circe-blower`)
  .dependsOn(`scalcite-circe-flattener`)
  .dependsOn(`scalcite-circe-table`)
  // P L U G I N S
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(DockerPlugin)
  .enablePlugins(GitVersioning)

lazy val `scalcite-ui` = (project in file("modules/scalcite-ui"))
  .configure(configurationNoPublish)
  .configure(Dependencies.`scalcite-ui`)
  // S E T T I N G S
  .settings(scalacOptions += "-Ymacro-annotations")
  .settings(useYarn := true)
  .settings(fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly())
  .settings(fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-fastopt.config.js"))
  .settings(fastOptJS / webpackDevServerExtraArgs := Seq("--inline", "--hot"))
  .settings(fullOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-opt.config.js"))
  .settings(startWebpackDevServer / version := "3.10.3")
  .settings(Test / requireJsDomEnv := true)
  .settings(Test / webpackConfigFile := Some(baseDirectory.value / "webpack" / "webpack-core.config.js"))
  .settings(webpack / version := "4.41.6")
  .settings(webpackResources := baseDirectory.value / "webpack" * "*")
  // P L U G I N S
  .enablePlugins(ScalaJSBundlerPlugin)

lazy val `scalcite-docs` = (project in file("modules/scalcite-docs"))
  .configure(configurationNoPublish)
  // S E T T I N G S
  .settings(scalaSource in Compile := baseDirectory.value / "src/main/mdoc")
  .settings(MdocSettings.value)
  .settings(libraryDependencies ++= Dependencies.`scalcite-docs`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-server` % "compile->test")
  // P L U G I N S
  .enablePlugins(MdocPlugin)

lazy val `scalcite-blower` = (project in file("modules/scalcite-blower"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(libraryDependencies ++= Dependencies.`scalcite-blower`)

lazy val `scalcite-circe-blower` = (project in file("modules/scalcite-circe/blower"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-blower`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower`)

lazy val `scalcite-flattener` = (project in file("modules/scalcite-flattener"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(libraryDependencies ++= Dependencies.`scalcite-flattener`)

lazy val `scalcite-circe-flattener` = (project in file("modules/scalcite-circe/flattener"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-flattener`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-flattener`)

lazy val `scalcite-circe-table` = (project in file("modules/scalcite-circe/table"))
  .configure(configurationPublish)
  // S E T T I N G S
  .settings(libraryDependencies ++= Dependencies.`scalcite-circe-table`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)
