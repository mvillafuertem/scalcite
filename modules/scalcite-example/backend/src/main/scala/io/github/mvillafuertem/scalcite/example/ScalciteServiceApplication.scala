package io.github.mvillafuertem.scalcite.example
import io.github.mvillafuertem.scalcite.example.configuration.{AkkaHttpConfiguration, ApiConfiguration, ScalciteServiceConfiguration}
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging.{LogFormat, LogLevel, Logging, log}

/**
 * @author Miguel Villafuerte
 */
object ScalciteServiceApplication extends ScalciteServiceConfiguration with zio.App {

  private lazy val loggingLayer: URLayer[Console with Clock, Logging] =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("ScalciteServiceApplication")

  private val program: ZIO[Logging, Nothing, ExitCode] =
    (for {
      routes <- ApiConfiguration.routes
      _      <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZScalciteEnv)
      .foldM(
        e => log.throwable("", e) as ExitCode.failure,
        _ => UIO.effectTotal(ExitCode.success)
      )

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    program.provideLayer(loggingLayer)

}
