package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import zio._

trait AkkaHttpConfiguration {

  def httpServer(route: Route): RIO[ZActorSystemConfiguration, Unit] =
    for {
      actorSystem <- ZIO.access[ZActorSystemConfiguration](_.get)
      scalciteConfigurationProperties <- Task(InfrastructureConfiguration.scalciteConfigurationProperties)
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

  type ZMaterializer = Has[Materializer]

  val materializerLive: ZLayer[ZActorSystemConfiguration, Nothing, ZMaterializer] =
    ZLayer.fromService[ActorSystem[_], Materializer](actorSystem => Materializer(actorSystem))

}

object AkkaHttpConfiguration extends AkkaHttpConfiguration
