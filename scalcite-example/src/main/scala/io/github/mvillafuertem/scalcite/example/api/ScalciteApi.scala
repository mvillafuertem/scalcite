package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.api.documentation.{ApiJsonCodec, ScalciteEndpoint}
import io.github.mvillafuertem.scalcite.example.api.domain.Queries
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.model.ScalciteSql
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import sttp.tapir.server.akkahttp._

import scala.concurrent.Future

final class ScalciteApi(scalciteApplication: ScalciteApplication[Source], queriesRepository: QueriesRepository[Source])
    extends ApiJsonCodec {

  val ping: Route =
  get {
    path("ping") {
      complete("PONG!\n")
    }

  } ~ queriesRoute ~ simulateRoute

  private lazy val queriesRoute: Route = ScalciteEndpoint.queriesEndpoint.toRoute { case (id, q) =>
    Future.successful {
      val value: Source[Map[String, Any], _] = queriesRepository.insert(ScalciteSql(id, q.queries.head))
      Right(
        value
          .map(e => Queries(Seq(e.values.head.toString)))
          .map(e => ByteString(e.asJson.noSpaces))
          .map(e => ByteString(e) ++ ByteString("\n"))
          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
      )
    }
  }

  private lazy val simulateRoute: Route = ScalciteEndpoint.simulateEndpoint.toRoute { case (id, j) =>
    Future.successful {
      val value: Source[String, _] = scalciteApplication.performJson(id, j.noSpaces)
      Right(
        value
          .map(e => ByteString(e))
          .map(e => ByteString(e) ++ ByteString("\n"))
          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
      )
    }
  }

}

object ScalciteApi {
  def apply(scalciteApplication: ScalciteApplication[Source],
            queriesRepository: QueriesRepository[Source]): ScalciteApi =
    new ScalciteApi(scalciteApplication, queriesRepository)
}
