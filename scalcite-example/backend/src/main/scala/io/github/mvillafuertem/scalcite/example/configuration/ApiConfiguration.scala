package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api._

trait ApiConfiguration {
  self: ApplicationConfiguration =>

  def route(implicit materializer: Materializer): Route =
    SwaggerApi.route ~
    ActuatorApi.route ~
    ErrorsApi(errorsApplicationLayer).route ~
    QueriesApi(queriesApplicationLayer).route ~
    ScalciteSimulateApi(scalciteApplicationLayer).route

}
