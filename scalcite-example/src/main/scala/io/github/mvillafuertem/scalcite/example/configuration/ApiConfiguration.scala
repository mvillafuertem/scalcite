package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.example.api.{ActuatorApi, ScalciteApi, SwaggerApi}
import io.github.mvillafuertem.scalcite.example.application.ScalciteApplicationImpl

trait ApiConfiguration {
  self: InfrastructureConfiguration =>

  private lazy val scalciteApplication = ScalciteApplicationImpl(queriesRepository, ScalciteServiceConfiguration.scalciteRepository)


  val route: Route = SwaggerApi.route ~
    ActuatorApi.route ~
    ScalciteApi(scalciteApplication, queriesRepository).ping
}
