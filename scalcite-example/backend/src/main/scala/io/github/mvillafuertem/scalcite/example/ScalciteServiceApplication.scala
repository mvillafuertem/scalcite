package io.github.mvillafuertem.scalcite.example
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.ZAkkaConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApiConfiguration.ZApiConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.{AkkaConfiguration, ApiConfiguration, ApplicationConfiguration, InfrastructureConfiguration, ScalciteServiceConfiguration}
import io.github.mvillafuertem.scalcite.example.configuration.properties.ScalciteConfigurationProperties
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
      routes <- ApiConfiguration.route
      _ <- AkkaConfiguration.httpServer(routes)
    } yield ())
      .provideLayer(ZScalciteEnv)
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program.provideLayer(loggingLayer)


}
