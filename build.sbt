lazy val `scalcite` = (project in file("."))
  .aggregate(
    `scalcite-core`,
    `scalcite-blower-core`,
    `scalcite-blower-circe`,
    `scalcite-flattener-core`,
    `scalcite-flattener-circe`,
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
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower-core`)
  .dependsOn(`scalcite-flattener-core`)

lazy val `scalcite-example` = (project in file("scalcite-example"))
  // C O N F I N G S
  .configs(IntegrationTest)
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(Defaults.itSettings)
  .settings(crossScalaVersions := Nil)
  .settings(libraryDependencies ++= Dependencies.`scalcite-example`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-core`)


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

lazy val `scalcite-blower-core` = (project in file("scalcite-blower/core"))
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-blower-core`)

lazy val `scalcite-blower-circe` = (project in file("scalcite-blower/circe"))
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-blower-circe`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-blower-core`)

lazy val `scalcite-flattener-core` = (project in file("scalcite-flattener/core"))
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-flattener-core`)

lazy val `scalcite-flattener-circe` = (project in file("scalcite-flattener/circe"))
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(crossScalaVersions := Settings.supportedScalaVersions)
  .settings(libraryDependencies ++= Dependencies.`scalcite-flattener-circe`)
  // D E P E N D S  O N
  .dependsOn(`scalcite-flattener-core`)
