package io.github.mvillafuertem.scalcite.server.configuration

import akka.actor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import ActorSystemConfiguration.ZActorSystemConfiguration
import InfrastructureConfiguration.ZInfrastructureConfiguration
import io.github.mvillafuertem.scalcite.server.api.SwaggerApi
import zio._

final class AkkaHttpConfiguration(infrastructureConfiguration: InfrastructureConfiguration, actorSystem: ActorSystem[_]) {

  def httpServer(route: Route): Task[Unit] =
    for {
      eventualBinding <- Task {
                          implicit lazy val untypedSystem: actor.ActorSystem = actorSystem.toClassic
                          Http().newServerAt(
                            infrastructureConfiguration.scalciteConfigurationProperties.interface,
                            infrastructureConfiguration.scalciteConfigurationProperties.port
                          ).bind(route)
                        }
      server <- Task
                 .fromFuture(_ => eventualBinding)
                 .tapError(exception =>
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

}

object AkkaHttpConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration, actorSystem: ActorSystem[_]): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(infrastructureConfiguration, actorSystem)

  type ZAkkaHttpConfiguration = Has[AkkaHttpConfiguration]
  type ZMaterializer          = Has[Materializer]

  def httpServer(route: Route): RIO[ZAkkaHttpConfiguration with ZActorSystemConfiguration, Unit] =
    ZIO.accessM(_.get.httpServer(route))

  val materializerLive: ZLayer[ZActorSystemConfiguration, Nothing, ZMaterializer] =
    ZLayer.fromService[ActorSystem[_], Materializer](Materializer.apply)

  val live: ZLayer[ZInfrastructureConfiguration with ZActorSystemConfiguration, Nothing, ZAkkaHttpConfiguration] =
    ZLayer.fromServices[InfrastructureConfiguration, ActorSystem[_], AkkaHttpConfiguration](AkkaHttpConfiguration(_, _))

}
