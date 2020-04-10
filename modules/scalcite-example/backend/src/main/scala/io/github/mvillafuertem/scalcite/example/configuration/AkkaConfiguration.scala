package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import zio._


final class AkkaConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {


  def httpServer(route: Route): ZIO[ZActorSystemConfiguration, Throwable, Unit] =
    for {
      actorSystem <- ZIO.access[ZActorSystemConfiguration](_.get)
      eventualBinding <- Task {
        implicit lazy val untypedSystem: actor.ActorSystem = actorSystem.toClassic
        implicit lazy val materializer: Materializer = Materializer(actorSystem)
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

  lazy val materializer: ZIO[ZActorSystemConfiguration, Throwable, Materializer] =
    for {
      as <- ZIO.access[ZActorSystemConfiguration](_.get)
      m <- Task(Materializer(as))
    } yield m

}

object AkkaConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): AkkaConfiguration =
    new AkkaConfiguration(infrastructureConfiguration)

  type ZAkkaConfiguration = Has[AkkaConfiguration]

  def httpServer(route: Route): RIO[ZAkkaConfiguration with ZActorSystemConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))


  val materializer: RIO[ZAkkaConfiguration with ZActorSystemConfiguration, Materializer] =
    ZIO.accessM(_.get.materializer)


  val live: ZLayer[ZInfrastructureConfiguration, Throwable, ZAkkaConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, AkkaConfiguration](
      infrastructureConfiguration => AkkaConfiguration(infrastructureConfiguration))


}