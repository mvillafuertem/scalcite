package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.DebuggingDirectives
import io.github.mvillafuertem.scalcite.BuildInfoScalcite
import io.github.mvillafuertem.scalcite.example.api.documentation.ActuatorEndpoint
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

/**
 * Actuator endpoint for monitoring application
 * http://host:port/api/v1.0/health
 */
trait ActuatorApi {

  // https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/debugging-directives/logRequestResult.html
  val route: Route = DebuggingDirectives.logRequestResult("actuator-logger") {
    ActuatorEndpoint.healthEndpoint.toRoute(_ => Future.successful(Right(BuildInfoScalcite.toMap)))
  }

}

object ActuatorApi extends ActuatorApi
