lazy val `scalcite` = (project in file("."))
  .aggregate(
    `scalcite-core`,
    `scalcite-example`,
    docs
  )
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)


lazy val `scalcite-core` = (project in file("scalcite-core"))
  .configs(IntegrationTest extend Test)
  // S E T T I N G S
  .settings(Settings.noAssemblyTest)
  .settings(Settings.value)
  .settings(libraryDependencies ++= Dependencies.`scalcite-core`)
  // P L U G I N S

lazy val `scalcite-example` = (project in file("scalcite-example"))
  .configs(IntegrationTest extend Test)
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
  .settings(Settings.noAssemblyTest)
  .settings(Defaults.itSettings)
  .settings(libraryDependencies ++= Dependencies.`scalcite-example`)
  // P L U G I N S


lazy val docs = (project in file("docs"))
  //  .dependsOn(`asset-seed-service` % "compile->test")
  // S E T T I N G S
  .settings(Settings.value)
  .settings(Settings.noPublish)
//  .settings(MdocSettings.value)
//  .settings(libraryDependencies ++= Dependencies.docs)
//  // P L U G I N S
//  .enablePlugins(MdocPlugin)
