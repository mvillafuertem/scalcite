package io.github.mvillafuertem.scalcite.example.api

import akka.NotUsed
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiErrorMapping, ApiJsonCodec, ScalciteEndpoint}
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import sttp.tapir.server.akkahttp._
import zio.interop.reactivestreams._
import zio.{BootstrapRuntime, stream}

import scala.concurrent.Future

final class ScalciteSimulateApi(scalciteApplication: ScalciteApplication)(implicit materializer: Materializer)
  extends ApiJsonCodec
    with ApiErrorMapping
    with BootstrapRuntime {

  val route: Route = queriesSimulateRoute

  lazy val queriesSimulateRoute: Route = ScalciteEndpoint.simulateEndpoint.toRoute {
    case (uuids, json) => buildResponse(scalciteApplication.performJson(json, uuids:_*).map(_.noSpaces))}


  private def buildResponse: stream.Stream[Throwable, String] => Future[Either[ScalciteError, Source[ByteString, NotUsed]]] = stream => {
    val value = unsafeRun(
      stream
        .map(query => ByteString(query) ++ ByteString("\n"))
        .toPublisher
    )
    Future.successful(
      Right(
        Source.fromPublisher(value)
          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
      )
    )
  }

}

object ScalciteSimulateApi {
  def apply(scalciteApplication: ScalciteApplication)(implicit materializer: Materializer): ScalciteSimulateApi = new ScalciteSimulateApi(scalciteApplication)(materializer)
}