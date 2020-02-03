package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route

trait ScalciteApi {

  val ping: Route =
    get {
      path("ping") {
        complete("PONG!\n")
      }

    }

}

object ScalciteApi extends ScalciteApi
