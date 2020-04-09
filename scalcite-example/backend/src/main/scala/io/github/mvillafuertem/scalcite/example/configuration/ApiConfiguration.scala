package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.example.api._
import io.github.mvillafuertem.scalcite.example.configuration.ActorSystemConfiguration.ZActorSystemConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.ZAkkaConfiguration
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio._

final class ApiConfiguration(applicationConfiguration: ApplicationConfiguration) {

  val route: ZIO[ZAkkaConfiguration with ZActorSystemConfiguration, Throwable, Route] =
    for {
      materializer <- AkkaConfiguration.materializer
      routes <- Task {
        SwaggerApi.route ~
        ActuatorApi.route ~
        ErrorsApi(applicationConfiguration.errorsApplication).route ~
        QueriesApi(applicationConfiguration.queriesApplication)(materializer).route ~
        ScalciteSimulateApi(applicationConfiguration.scalciteApplication).route
      }
    } yield routes

}


object ApiConfiguration {

  def apply(applicationConfiguration: ApplicationConfiguration): ApiConfiguration =
    new ApiConfiguration(applicationConfiguration)

  type ZApiConfiguration = Has[ApiConfiguration]

  val route: RIO[ZApiConfiguration with ZAkkaConfiguration with ZActorSystemConfiguration, Route] =
    ZIO.accessM(_.get.route)

  val live: ZLayer[ZApplicationConfiguration, Throwable, ZApiConfiguration] =
    ZLayer.fromService[ApplicationConfiguration, ApiConfiguration](
      (applicationConfiguration) => ApiConfiguration(applicationConfiguration)
    )

}

