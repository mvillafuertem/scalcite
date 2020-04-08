package io.github.mvillafuertem.scalcite.example
import akka.actor.typed.ActorSystem
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging.Logging
import zio.logging.{Logging, log}

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
object ScalciteServiceApplication extends ScalciteServiceConfiguration with zio.App {

  private val loggingLayer: URLayer[Console with Clock, Logging] =
    Logging.console((_, logEntry) => logEntry)

  private val executionContextLayer: ULayer[Has[ExecutionContext]] =
    ZLayer.succeed(platform.executor.asEC)

  private val live: TaskLayer[Has[ActorSystem[_]]] =
    (executionContextLayer >>> actorSystemLayer)

  val program: ZIO[Logging, Nothing, Int] =
    (for {
      _ <- httpServer
    } yield 0)
      .provideLayer(live)
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program.provideLayer(loggingLayer)


}
