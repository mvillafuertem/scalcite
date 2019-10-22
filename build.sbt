lazy val `scalcite` = (project in file("."))
  .aggregate(
    `scalcite-core`,
    `scalcite-example`,
    `scalcite-docs`
  )
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(crossScalaVersions := Nil)


lazy val `scalcite-core` = (project in file("scalcite-core"))
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noAssemblyTest)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-core`)
  // P L U G I N S

lazy val `scalcite-example` = (project in file("scalcite-example"))
  .dependsOn(`scalcite-core`)
  // C O N F I N G S
  .configs(IntegrationTest)
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(Defaults.itSettings)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-example`)
  // P L U G I N S


lazy val `scalcite-docs` = (project in file("scalcite-docs"))
  .dependsOn(`scalcite-example` % "compile->test")
  // S E T T I N G S
  .settings(scalaSource in Compile := baseDirectory.value / "src/main/mdoc")
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(MdocSettings.value)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-docs`)
  // P L U G I N S
  .enablePlugins(MdocPlugin)
