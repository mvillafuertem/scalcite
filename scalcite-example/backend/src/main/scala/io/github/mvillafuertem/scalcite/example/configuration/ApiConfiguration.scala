package io.github.mvillafuertem.scalcite.example.configuration

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import io.github.mvillafuertem.scalcite.example.api._
import zio.{Has, ZLayer}

trait ApiConfiguration {
  self: ApplicationConfiguration with AkkaConfiguration =>

  lazy val routeLayer: ZLayer[Any, Throwable, Has[Route]] =
    materializerLayer >>> ZLayer.fromService[Materializer, Route] ( implicit materializer => route)

  def route(implicit materializer: Materializer): Route =
    SwaggerApi.route ~
    ActuatorApi.route ~
    ErrorsApi(errorsApplicationLayer).route ~
    QueriesApi(queriesApplicationLayer).route ~
    ScalciteSimulateApi(scalciteApplicationLayer).route

}
