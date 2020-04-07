package io.github.mvillafuertem.scalcite.example.api

import akka.NotUsed
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiErrorMapping, ApiJsonCodec, ErrorsEndpoint}
import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import sttp.tapir.server.akkahttp._
import zio.interop.reactivestreams._
import zio.{BootstrapRuntime, stream}

import scala.concurrent.Future

final class ErrorsApi(errorsApplication: ErrorsApplication)(implicit materializer: Materializer)
  extends ApiJsonCodec
    with ApiErrorMapping
    with BootstrapRuntime {

  val route: Route =
      errorsGetRoute ~
      errorsGetAllRoute

  lazy val errorsGetRoute: Route = ErrorsEndpoint.errorsGetEndpoint.toRoute {
    uuid => buildResponse(errorsApplication.findByUUID(uuid).map(_.asJson.noSpaces))}

  lazy val errorsGetAllRoute: Route = ErrorsEndpoint.errorsGetAllEndpoint.toRoute {
    _ => buildResponse(errorsApplication.findAll().map(_.asJson.noSpaces))}

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

object ErrorsApi {
  def apply(errorsApplication: ErrorsApplication)(implicit materializer: Materializer): ErrorsApi = new ErrorsApi(errorsApplication)(materializer)
}
