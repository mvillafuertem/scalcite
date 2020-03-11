import sbt.Command

object Commands {

  val FrontendDevCommand = Command.command("dev") {
    state =>
      "project scalcite-example-frontend" :: "fastOptJS::startWebpackDevServer" :: "~fastOptJS" :: state
  }

  val FrontendBuildCommand = Command.command("build") {
    state =>
      "project scalcite-example-frontend" :: "fullOptJS::webpack" :: state
  }


  val value = Seq(
    FrontendDevCommand,
    FrontendBuildCommand
  )

}
