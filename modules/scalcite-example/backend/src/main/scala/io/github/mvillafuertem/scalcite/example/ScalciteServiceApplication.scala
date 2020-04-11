package io.github.mvillafuertem.scalcite.example
import io.github.mvillafuertem.scalcite.example.configuration.{AkkaHttpConfiguration, ApiConfiguration, ScalciteServiceConfiguration}
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging.Logging
import zio.logging.{Logging, log}

/**
 * @author Miguel Villafuerte
 */
object ScalciteServiceApplication extends ScalciteServiceConfiguration with zio.App {

  private val loggingLayer: URLayer[Console with Clock, Logging] =
    Logging.console((_, logEntry) => logEntry)

  private val program: ZIO[Logging, Nothing, Int] =
    (for {
      routes <- ApiConfiguration.routes
      _ <- AkkaHttpConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZScalciteEnv)
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program.provideLayer(loggingLayer)

}
