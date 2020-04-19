package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api._
import io.github.mvillafuertem.scalcite.example.configuration.AkkaHttpConfiguration.ZMaterializer
import io.github.mvillafuertem.scalcite.example.configuration.ApplicationConfiguration.ZApplicationConfiguration
import zio._

final class ApiConfiguration(applicationConfiguration: ApplicationConfiguration,
                             materializer: Materializer) {

  val errorsApi: ErrorsApi =
    ErrorsApi(applicationConfiguration.errorsApplication)

  val queriesApi: QueriesApi =
    QueriesApi(applicationConfiguration.queriesApplication)(materializer)

  val scalciteSimulateApi: ScalciteSimulateApi =
    ScalciteSimulateApi(applicationConfiguration.scalciteApplication)

}


object ApiConfiguration {

  def apply(applicationConfiguration: ApplicationConfiguration, materializer: Materializer): ApiConfiguration =
    new ApiConfiguration(applicationConfiguration, materializer)

  type ZApiConfiguration = Has[ApiConfiguration]

  val routes: ZIO[ZApiConfiguration, Nothing, Route] =
    for {
      errorsRoute <- ZIO.access[ZApiConfiguration](_.get.errorsApi.route)
      queriesRoute <- ZIO.access[ZApiConfiguration](_.get.queriesApi.route)
      scalciteSimulateRoute <- ZIO.access[ZApiConfiguration](_.get.scalciteSimulateApi.route)

    } yield errorsRoute ~
      queriesRoute ~
      scalciteSimulateRoute ~
      SwaggerApi.route ~
      ActuatorApi.route

  val live: ZLayer[ZApplicationConfiguration with ZMaterializer, Throwable, ZApiConfiguration] =
    ZLayer.fromServices[ApplicationConfiguration, Materializer, ApiConfiguration](
      ApiConfiguration.apply)

}

