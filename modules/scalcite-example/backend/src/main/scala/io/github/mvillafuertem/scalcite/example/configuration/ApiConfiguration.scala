package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.example.api.ErrorsApi.ZErrorsApi
import io.github.mvillafuertem.scalcite.example.api.QueriesApi.ZQueriesApi
import io.github.mvillafuertem.scalcite.example.api.ScalciteSimulateApi.ZScalciteSimulateApi
import io.github.mvillafuertem.scalcite.example.api._
import io.github.mvillafuertem.scalcite.example.application.ErrorsService.ZErrorsApplication
import io.github.mvillafuertem.scalcite.example.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer.ZScalciteApplication
import io.github.mvillafuertem.scalcite.example.configuration.AkkaHttpConfiguration.ZMaterializer
import zio._

trait ApiConfiguration {

  type ZApiConfiguration = ZScalciteSimulateApi with ZQueriesApi with ZErrorsApi

  val routes: ZIO[ZApiConfiguration, Nothing, Route] =
    for {
      errorsRoute <- ErrorsApi.route
      queriesRoute <- QueriesApi.route
      scalciteSimulateRoute <- ScalciteSimulateApi.route
    } yield errorsRoute ~
      queriesRoute ~
      scalciteSimulateRoute ~
      SwaggerApi.route ~
      ActuatorApi.route

  val live: ZLayer[ZScalciteApplication with ZQueriesApplication with ZMaterializer with ZErrorsApplication, Nothing, ZApiConfiguration] =
    ScalciteSimulateApi.live ++ QueriesApi.live ++ ErrorsApi.live
}

object ApiConfiguration extends ApiConfiguration

