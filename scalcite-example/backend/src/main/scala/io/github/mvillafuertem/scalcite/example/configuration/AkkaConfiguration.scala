package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor.BootstrapSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.{Done, actor}
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import zio._

import scala.concurrent.ExecutionContext


trait AkkaConfiguration {
  self: ApiConfiguration with InfrastructureConfiguration =>

  val actorSystemLayer: RLayer[Has[ExecutionContext], Has[ActorSystem[_]]] =
    ZLayer.fromAcquireRelease(actorSystem)(sys => UIO.succeed(sys.terminate()).ignore)

  private lazy val actorSystem: RIO[Has[ExecutionContext], ActorSystem[_]] =
    for {
      executionContext <- ZIO.access[Has[ExecutionContext]](_.get)
      actorSystem <- Task(
        ActorSystem[Done](
          Behaviors.setup[Done] { context =>
            context.setLoggerName(this.getClass)
            context.log.info(s"Starting ${scalciteConfigurationProperties.name}... ${"BuildInfo.toJson"}")
            Behaviors.receiveMessage {
              case Done =>
                context.log.error(s"Server could not start!")
                Behaviors.stopped
            }
          },
          scalciteConfigurationProperties.name.toLowerCase(),
          BootstrapSetup().withDefaultExecutionContext(executionContext)
        )
      )
    } yield actorSystem




  val httpServer: RIO[Has[ActorSystem[_]], Unit] =
    for {
      actorSystem <- ZIO.access[Has[ActorSystem[_]]](_.get)
      eventualBinding <- Task {
        implicit lazy val untypedSystem: actor.ActorSystem = actorSystem.toClassic
        implicit lazy val materializer: Materializer = Materializer(actorSystem)
        Http().bindAndHandle(route, scalciteConfigurationProperties.interface, scalciteConfigurationProperties.port)
      }
      server <- Task
        .fromFuture(_ => eventualBinding)
        .tapError(
          exception =>
            UIO(
              actorSystem.log.error(
                s"Server could not start with parameters [host:port]=[${scalciteConfigurationProperties.interface},${scalciteConfigurationProperties.port}]",
                exception
              )
            )
        )
        .forever
        .fork
      _ <- UIO(
        actorSystem.log.info(
          s"Server online at http://${scalciteConfigurationProperties.interface}:${scalciteConfigurationProperties.port}/${SwaggerApi.swagger}"
        )
      )
      _ <- server.join
    } yield ()

}