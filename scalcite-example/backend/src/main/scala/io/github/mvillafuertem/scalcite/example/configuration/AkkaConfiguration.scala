package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor.BootstrapSetup
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.{Done, actor}
import io.github.mvillafuertem.scalcite.example.ScalciteServiceApplication.platform
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import io.github.mvillafuertem.scalcite.example.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._

import scala.concurrent.ExecutionContext


final class AkkaConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {

  lazy val executionContext: Task[ExecutionContext] = Task(platform.executor.asEC)

  def httpServer(route: Route): Task[Unit] =
    for {
    actorSystem <- actorSystem
    materializer <- materializer
    eventualBinding <- Task {
      implicit lazy val untypedSystem: actor.ActorSystem = actorSystem.toClassic
      implicit lazy val mat: Materializer = materializer
      Http().bindAndHandle(route, infrastructureConfiguration.scalciteConfigurationProperties.interface, infrastructureConfiguration.scalciteConfigurationProperties.port)
    }
    server <- Task
      .fromFuture(_ => eventualBinding)
      .tapError(
        exception =>
          UIO(
            actorSystem.log.error(
              s"Server could not start with parameters [host:port]=[${infrastructureConfiguration.scalciteConfigurationProperties.interface},${infrastructureConfiguration.scalciteConfigurationProperties.port}]",
              exception
            )
          )
      )
      .forever
      .fork
    _ <- UIO(
      actorSystem.log.info(
        s"Server online at http://${infrastructureConfiguration.scalciteConfigurationProperties.interface}:${infrastructureConfiguration.scalciteConfigurationProperties.port}/${SwaggerApi.swagger}"
      )
    )
    _ <- server.join
  } yield ()

  lazy val actorSystem: Task[ActorSystem[Done]] =
    for {
      executionContext <- executionContext
      actorSystem <- Task(
        ActorSystem[Done](
          Behaviors.setup[Done] { context =>
            context.setLoggerName(this.getClass)
            context.log.info(s"Starting ${infrastructureConfiguration.scalciteConfigurationProperties.name}... ${"BuildInfo.toJson"}")
            Behaviors.receiveMessage {
              case Done =>
                context.log.error(s"Server could not start!")
                Behaviors.stopped
            }
          },
          infrastructureConfiguration.scalciteConfigurationProperties.name.toLowerCase(),
          BootstrapSetup().withDefaultExecutionContext(executionContext)
        )
      )
    } yield actorSystem

  lazy val materializer: Task[Materializer] =
    for {
      as <- actorSystem
      m <- Task(Materializer(as))
    } yield m

}

object AkkaConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): AkkaConfiguration =
    new AkkaConfiguration(infrastructureConfiguration)

  type ZAkkaConfiguration = Has[AkkaConfiguration]

  def httpServer(route: Route): RIO[ZAkkaConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))

  val actorSystem: ZIO[ZAkkaConfiguration,Throwable, ActorSystem[_]] =
    ZIO.accessM(_.get.actorSystem)

  val materializer: RIO[ZAkkaConfiguration, Materializer] =
    ZIO.accessM(_.get.materializer)

  val executionContext: RIO[ZAkkaConfiguration, ExecutionContext] =
    ZIO.accessM(_.get.executionContext)

  val live: ZLayer[ZInfrastructureConfiguration, Throwable, ZAkkaConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, AkkaConfiguration](
      infrastructureConfiguration => AkkaConfiguration(infrastructureConfiguration))
}