package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor.BootstrapSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.{Done, actor}
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import zio.{Task, UIO, ZIO}

import scala.concurrent.ExecutionContext

trait ScalciteServiceConfiguration extends ApiConfiguration
  with  InfrastructureConfiguration with  ApplicationConfiguration {

  implicit val executionContext: ExecutionContext

  val actorSystem: Task[ActorSystem[Done]] = Task(
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


  def httpServer(actorSystem: ActorSystem[_]): ZIO[Any, Throwable, Unit] = {

    implicit lazy val untypedSystem: actor.ActorSystem = actorSystem.toClassic
    implicit lazy val materializer: Materializer = Materializer(actorSystem)

    for {
      eventualBinding <- Task(
        Http().bindAndHandle(route, scalciteConfigurationProperties.interface, scalciteConfigurationProperties.port)
      )
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

}
