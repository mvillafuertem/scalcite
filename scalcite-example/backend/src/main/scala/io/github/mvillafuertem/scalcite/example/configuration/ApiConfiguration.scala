package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api.{ActuatorApi, ScalciteApi, SwaggerApi}

import scala.concurrent.ExecutionContext

trait ApiConfiguration {
  self: ApplicationConfiguration =>

  implicit val executionContext: ExecutionContext

  def route(implicit materializer: Materializer): Route = SwaggerApi.route ~
    ActuatorApi.route ~
    ScalciteApi(scalciteApplication).route
}