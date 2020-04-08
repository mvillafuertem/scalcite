package io.github.mvillafuertem.scalcite.example.api

import akka.NotUsed
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiErrorMapping, ApiJsonCodec, ScalciteEndpoint}
import io.github.mvillafuertem.scalcite.example.application.QueriesService
import io.github.mvillafuertem.scalcite.example.application.QueriesService.QueriesApp
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import org.reactivestreams.Publisher
import sttp.tapir.server.akkahttp._
import zio.interop.reactivestreams._
import zio.{BootstrapRuntime, ULayer, stream}

import scala.concurrent.Future

final class QueriesApi(env: ULayer[QueriesApp])(implicit materializer: Materializer)
  extends ApiJsonCodec
    with ApiErrorMapping
    with BootstrapRuntime {

  val route: Route =
  get {
    path("ping") {
      complete("PONG!\n")
    }
  } ~
    queriesPostRoute ~
    queriesGetRoute ~
    queriesGetAllRoute

  lazy val queriesPostRoute: Route = ScalciteEndpoint.queriesPostEndpoint.toRoute {
    query => buildScalciteResponse(QueriesService.create(query).map(_.asJson.noSpaces).provideLayer(env))}

  lazy val queriesGetRoute: Route = ScalciteEndpoint.queriesGetEndpoint.toRoute {
    uuid => buildResponse(QueriesService.findByUUID(uuid).map(_.asJson.noSpaces).provideLayer(env))}

  lazy val queriesGetAllRoute: Route = ScalciteEndpoint.queriesGetAllEndpoint.toRoute {
    _ => buildResponse(QueriesService.findAll().map(_.asJson.noSpaces).provideLayer(env))}


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

  private def buildScalciteResponse: stream.Stream[ScalciteError, String] => Future[Either[ScalciteError, Source[ByteString, NotUsed]]] = stream => {
    val value: Publisher[Either[ScalciteError, String]] = unsafeRun(
      stream
        .either
        .toPublisher
    )

    Source
      .fromPublisher(value)
      .via(flow)
      .toMat(Sink.head)(Keep.right).run()
  }

  val flow: Flow[Either[ScalciteError, String], Either[ScalciteError, Source[ByteString, NotUsed]], NotUsed] =
    Flow[Either[ScalciteError, String]]
      .map {
        case Left(value) => Left(value)
        case Right(value) => Right(
          Source
            .single(value)
            .map(query => ByteString(query) ++ ByteString("\n"))
            .intersperse(ByteString("["), ByteString(","), ByteString("]"))
        )
      }

}

object QueriesApi {
  def apply(env: ULayer[QueriesApp])(implicit materializer: Materializer): QueriesApi = new QueriesApi(env)(materializer)
}
