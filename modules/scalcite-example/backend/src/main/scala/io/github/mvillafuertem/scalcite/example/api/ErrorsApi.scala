package io.github.mvillafuertem.scalcite.example.api

import akka.NotUsed
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiErrorMapping, ApiJsonCodec, ErrorsEndpoint}
import io.github.mvillafuertem.scalcite.example.application.ErrorsService.ZErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import sttp.tapir.server.akkahttp._
import zio.interop.reactivestreams._
import zio._

import scala.concurrent.Future

final class ErrorsApi(app: ErrorsApplication) extends ApiJsonCodec
  with ApiErrorMapping
  with BootstrapRuntime {

  val route: Route =
      errorsGetRoute ~
      errorsGetAllRoute

  lazy val errorsGetRoute: Route = ErrorsEndpoint.errorsGetEndpoint.toRoute {
    uuid => buildResponse(app.findByUUID(uuid).map(_.asJson.noSpaces))}

  lazy val errorsGetAllRoute: Route = ErrorsEndpoint.errorsGetAllEndpoint.toRoute {
    _ => buildResponse(app.findAll().map(_.asJson.noSpaces))}

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

  def apply(app: ErrorsApplication): ErrorsApi = new ErrorsApi(app)

  type ZErrorsApi = Has[ErrorsApi]

  val route: ZIO[ZErrorsApi, Nothing, Route] =
    ZIO.access[ZErrorsApi](_.get.route)

  val live: ZLayer[ZErrorsApplication, Nothing, ZErrorsApi] =
    ZLayer.fromService[ErrorsApplication, ErrorsApi](ErrorsApi.apply)

}
