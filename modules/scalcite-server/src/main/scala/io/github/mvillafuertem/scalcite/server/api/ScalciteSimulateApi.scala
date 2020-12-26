package io.github.mvillafuertem.scalcite.server.api

import akka.NotUsed
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.github.mvillafuertem.scalcite.server.api.documentation.{ ApiErrorMapping, ApiJsonCodec, ScalciteEndpoint }
import io.github.mvillafuertem.scalcite.server.application.ScalcitePerformer.ZScalciteApplication
import io.github.mvillafuertem.scalcite.server.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError
import sttp.tapir.server.akkahttp._
import zio._
import zio.interop.reactivestreams._

import scala.concurrent.Future

final class ScalciteSimulateApi(app: ScalciteApplication) extends ApiJsonCodec with ApiErrorMapping with BootstrapRuntime {

  val route: Route = queriesSimulateRoute

  lazy val queriesSimulateRoute: Route = AkkaHttpServerInterpreter
    .toRoute(ScalciteEndpoint.simulateEndpoint) { case (uuids, json) =>
      buildResponse(app.performJson(json, uuids: _*).map(_.noSpaces))
    }

  private def buildResponse: stream.Stream[Throwable, String] => Future[Either[ScalciteError, Source[ByteString, NotUsed]]] = stream => {
    val value = unsafeRun(
      stream
        .map(query => ByteString(query) ++ ByteString("\n"))
        .toPublisher
    )
    Future.successful(
      Right(
        Source
          .fromPublisher(value)
          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
      )
    )
  }

}

object ScalciteSimulateApi {

  def apply(app: ScalciteApplication): ScalciteSimulateApi = new ScalciteSimulateApi(app)

  type ZScalciteSimulateApi = Has[ScalciteSimulateApi]

  val route: ZIO[ZScalciteSimulateApi, Nothing, Route] =
    ZIO.access[ZScalciteSimulateApi](_.get.route)

  val live: ZLayer[ZScalciteApplication, Nothing, ZScalciteSimulateApi] =
    ZLayer.fromService[ScalciteApplication, ScalciteSimulateApi](ScalciteSimulateApi.apply)

}
