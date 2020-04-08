package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.circe.Json
import io.circe.scalcite.blower.ScalciteBlower._
import io.circe.scalcite.flattener.ScalciteFlattener._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.blower.Blower._
import io.github.mvillafuertem.scalcite.example.api.documentation.ApiJsonCodec._
import io.github.mvillafuertem.scalcite.example.application.QueriesService.QueriesApp
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.repository.CalciteRepository
import io.github.mvillafuertem.scalcite.example.domain.{QueriesApplication, ScalciteApplication}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepository.CalciteRepo
import io.github.mvillafuertem.scalcite.flattener.Flattener._
import zio._
import zio.stream.ZStream

private final class ScalcitePerformer(app: QueriesApplication, repository: CalciteRepository) extends ScalciteApplication {

  override def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]] =
    ZStream.fromIterable(uuid)
      .flatMapPar(10)(uuid => app.findByUUID(uuid))
      .flatMapPar(10)(query => repository.queryForMap(map, query.value)
        .either
        .map(_.fold(_ => Map.empty[String, Any], identity)))

  override def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json] =
    for {
      flattened <- flattener(json)
      result <- ZStream.fromIterable(uuid)
        .flatMapPar(1)(uuid => app.findByUUID(uuid).tap(_ => UIO(Thread.sleep(1000))))
        .flatMapPar(1)(query => performQuery(flattened, query.value))
      blowed <- blower(result)
    } yield blowed

  private def performQuery(json: Json, query: String): stream.Stream[Nothing, Json] =
    repository
      .queryForJson(json, query)
      .either
      .map(_.fold(_.asJson, identity))

  private def flattener(json: Json): stream.Stream[Nothing, Json] =
    ZStream.fromEffect(IO.fromEither(json.flatten))
      .either
      .map(_.fold(e => ScalciteError.Unknown(e.getMessage).asJson, identity))

  private def blower(json: Json): stream.Stream[Nothing, Json] =
    ZStream.fromEffect(IO.fromEither(json.asJson.blow))
      .either
      .map(_.fold(e => ScalciteError.Unknown(e.getMessage).asJson, identity))
}

object ScalcitePerformer {

  type ScalciteApp = Has[ScalciteApplication]

  def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.ZStream[ScalciteApp, Throwable, collection.Map[String, Any]] =
    stream.ZStream.accessStream(_.get.performMap(map, uuid:_*))

  def performJson(json: Json, uuid: UUID*): stream.ZStream[ScalciteApp, Throwable, Json] =
    stream.ZStream.accessStream(_.get.performJson(json, uuid:_*))

  val live: URLayer[QueriesApp with CalciteRepo, ScalciteApp] =
    ZLayer.fromServices[QueriesApplication, CalciteRepository, ScalciteApplication](
      (app, repository) => new ScalcitePerformer(app, repository))

}