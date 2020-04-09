package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.example.api._
import io.github.mvillafuertem.scalcite.example.configuration.AkkaConfiguration.{ZAkkaConfiguration, ZAkkaSystemConfiguration}
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio._

object ApiConfiguration {

  def apply(applicationConfiguration: ApplicationConfiguration,
            akkaConfiguration: AkkaConfiguration): ApiConfiguration =
    new ApiConfiguration(applicationConfiguration, akkaConfiguration)

  type ZApiConfiguration = Has[ApiConfiguration]

  val route: RIO[ZApiConfiguration with ZAkkaConfiguration with ZAkkaSystemConfiguration, Route] =
    ZIO.accessM(_.get.route)

  val live: ZLayer[ZApplicationConfiguration with ZAkkaConfiguration, Throwable, ZApiConfiguration] =
    ZLayer.fromServices[ApplicationConfiguration, AkkaConfiguration, ApiConfiguration](
      (applicationConfiguration, akkaConfiguration) => ApiConfiguration(applicationConfiguration, akkaConfiguration)
    )

}

final class ApiConfiguration(applicationConfiguration: ApplicationConfiguration, akkaConfiguration: AkkaConfiguration) {

  val route: ZIO[ZAkkaConfiguration with ZAkkaSystemConfiguration, Throwable, Route] =
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
