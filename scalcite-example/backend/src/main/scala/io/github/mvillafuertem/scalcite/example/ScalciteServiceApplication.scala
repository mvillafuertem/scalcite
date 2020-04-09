package io.github.mvillafuertem.scalcite.example
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
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

  val ScalciteEnv: ZLayer[Any, Throwable, Has[Materializer] with Has[Route] with Has[ActorSystem[_]] with Has[ScalciteConfigurationProperties]] =
    materializerLayer ++
      routeLayer ++
      actorSystemLayer ++
      scalciteConfigurationPropertiesLayer

  val program: ZIO[Logging, Nothing, Int] =
    (for {
      _ <- httpServer
    } yield 0)
      .provideLayer(ScalciteEnv)
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program.provideLayer(loggingLayer)


}
