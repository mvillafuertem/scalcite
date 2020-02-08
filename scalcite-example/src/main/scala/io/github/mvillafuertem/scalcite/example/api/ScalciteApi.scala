package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiJsonCodec, ScalciteEndpoint}
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import sttp.tapir.server.akkahttp._
import zio.{DefaultRuntime, UIO}
import zio.interop.reactiveStreams._

import scala.concurrent.ExecutionContext

final class ScalciteApi(scalciteApplication: ScalciteApplication)(implicit executionContext: ExecutionContext) extends ApiJsonCodec with DefaultRuntime {

  val route: Route =
  get {
    path("ping") {
      complete("PONG!\n")
    }
  } ~ queriesRoute //~ simulateRoute

  private lazy val queriesRoute: Route = ScalciteEndpoint.queriesEndpoint.toRoute { dto =>
      unsafeRunToFuture(scalciteApplication.createQuery(Query(value = dto.value))
        .map(_.asJson.noSpaces)
        .map(query => ByteString(query) ++ ByteString("\n")).toPublisher)
        .map(publisher =>
          Right(Source.fromPublisher(publisher)
            .intersperse(ByteString("["), ByteString(","), ByteString("]"))))
    }


  private lazy val simulateRoute: Route = ???
//  ScalciteEndpoint.simulateEndpoint.toRoute { case (id, j) =>
//    Future.successful {
//      val value: Source[String, _] = scalciteApplication.performJson(id, j.noSpaces)
//      Right(
//        value
//          .map(e => ByteString(e))
//          .map(e => ByteString(e) ++ ByteString("\n"))
//          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
//      )
//    }
//  }

}

object ScalciteApi {
  def apply(scalciteApplication: ScalciteApplication)(implicit executionContext: ExecutionContext): ScalciteApi = new ScalciteApi(scalciteApplication)(executionContext)
}
