package io.github.mvillafuertem.scalcite.example.configuration

import akka.actor
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api.SwaggerApi
import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.ZAkkaConfiguration
import zio.{Has, RIO, Task, UIO, ZIO, ZLayer}

final class AkkaHttpConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {



}

object AkkaHttpConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): AkkaHttpConfiguration =
    new AkkaHttpConfiguration(infrastructureConfiguration)

  type ZAkkaHttpConfiguration = Has[AkkaHttpConfiguration]

//  def httpServer(route: Route): RIO[ZAkkaConfiguration with ZAkkaHttpConfiguration, Unit] =
//    ZIO.accessM(_.get.httpServer(route))
}
