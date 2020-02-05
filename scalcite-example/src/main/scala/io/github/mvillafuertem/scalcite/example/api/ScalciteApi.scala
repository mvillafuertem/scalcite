package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import io.github.mvillafuertem.scalcite.example.api.documentation.ApiJsonCodec
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.RelationalQueriesRepository.TypeZStream

final class ScalciteApi(scalciteApplication: ScalciteApplication[Source], queriesRepository: QueriesRepository[TypeZStream])
    extends ApiJsonCodec {

  val ping: Route =
  get {
    path("ping") {
      complete("PONG!\n")
    }

  } ~ queriesRoute ~ simulateRoute

  private lazy val queriesRoute: Route = ???

//  ScalciteEndpoint.queriesEndpoint.toRoute { case (id, q) =>
//    Future.successful {
//      val value: Source[Map[String, Any], _] = queriesRepository.insert(Query(id, q.queries.head))
//      Right(
//        value
//          .map(e => Queries(Seq(e.values.head.toString)))
//          .map(e => ByteString(e.asJson.noSpaces))
//          .map(e => ByteString(e) ++ ByteString("\n"))
//          .intersperse(ByteString("["), ByteString(","), ByteString("]"))
//      )
//    }
//  }

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
  def apply(scalciteApplication: ScalciteApplication[Source],
            queriesRepository: QueriesRepository[TypeZStream]): ScalciteApi =
    new ScalciteApi(scalciteApplication, queriesRepository)
}
