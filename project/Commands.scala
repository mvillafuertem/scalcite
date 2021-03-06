import sbt.Command

object Commands {

  val FrontendDevCommand = Command.command("dev") { state =>
    "project scalcite-example-frontend" :: "fastOptJS::startWebpackDevServer" :: "~fastOptJS" :: state
  }

  val FrontendBuildCommand = Command.command("build")(state => "project scalcite-example-frontend" :: "fullOptJS::webpack" :: state)

  val FmtSbtCommand = Command.command("fmt")(state => "scalafmtSbt" :: "scalafmt" :: "test:scalafmt" :: state)

  val FmtSbtCheckCommand = Command.command("check")(state => "scalafmtSbtCheck" :: "scalafmtCheck" :: "test:scalafmtCheck" :: state)

  val UpInfrastructureCommand = Command.command("upinfra") { state =>
    Infrastructure.up(state)
    state
  }

  val DownInfrastructureCommand = Command.command("downinfra") { state =>
    Infrastructure.down(state)
    state
  }

  val value = Seq(
    FrontendDevCommand,
    FrontendBuildCommand,
    FmtSbtCommand,
    FmtSbtCheckCommand,
    UpInfrastructureCommand,
    DownInfrastructureCommand
  )

}
