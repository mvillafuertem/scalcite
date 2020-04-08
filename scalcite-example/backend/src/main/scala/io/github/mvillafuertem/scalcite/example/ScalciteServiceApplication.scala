package io.github.mvillafuertem.scalcite.example
import akka.actor.typed.ActorSystem
import io.github.mvillafuertem.scalcite.example.configuration.{AkkaConfiguration, ScalciteServiceConfiguration}
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

  private val actorSystemLayer: RLayer[Has[ExecutionContext], Has[ActorSystem[_]]] =
    ZLayer.fromAcquireRelease(actorSystem)(sys => UIO.succeed(sys.terminate()).ignore)

  private val executionContextLayer: ULayer[Has[ExecutionContext]] =
    ZLayer.succeed(platform.executor.asEC)

  val program: ZIO[Has[ActorSystem[_]], Throwable, Int] =
    (for {
      _ <- httpServer
    } yield 0)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program
      .provideLayer(executionContextLayer >>> actorSystemLayer)
      .foldM(e => log.throwable("", e).as(1), _ => UIO.effectTotal(0))
      .provideLayer(loggingLayer)
}
