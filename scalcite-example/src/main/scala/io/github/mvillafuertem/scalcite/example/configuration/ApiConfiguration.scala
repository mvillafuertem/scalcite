package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.example.api.{ActuatorApi, ScalciteApi, SwaggerApi}

trait ApiConfiguration {

  val route: Route = SwaggerApi.route ~ ActuatorApi.route ~ ScalciteApi.ping
}

object ApiConfiguration extends ApiConfiguration
