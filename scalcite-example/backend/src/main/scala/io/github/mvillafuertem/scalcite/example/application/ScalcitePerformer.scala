package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.circe.Json
import io.circe.scalcite.blower.ScalciteBlower._
import io.circe.scalcite.flattener.ScalciteFlattener._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.blower.Blower._
import io.github.mvillafuertem.scalcite.example.api.documentation.ApiJsonCodec._
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.repository.CalciteRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository
import io.github.mvillafuertem.scalcite.flattener.Flattener._
import zio.stream.ZStream
import zio.{IO, UIO, stream}


final class ScalcitePerformer(calcite: CalciteRepository) extends ScalciteApplication {


  val liveEnv = RelationalQueriesRepository.live >>> QueriesService.live


  override def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]] =
    ZStream.fromIterable(uuid)
        .flatMapPar(10)(uuid => QueriesService.findByUUID(uuid).provideLayer(liveEnv).provide("queriesdb"))
        .flatMapPar(10)(query => calcite.queryForMap(map, query.value))

  override def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json] =
    for {
      flattened <- flattener(json)
      result <- ZStream.fromIterable(uuid)
        .flatMapPar(1)(uuid => QueriesService.findByUUID(uuid).tap(_ => UIO(Thread.sleep(1000))).provideLayer(liveEnv).provide("queriesdb"))
        .flatMapPar(1)(query => performQuery(flattened, query.value))
      blowed <- blower(result)
    } yield blowed


  private def performQuery(json: Json, query: String): stream.Stream[Nothing, Json] =
    calcite
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
  def apply(calcite: CalciteRepository): ScalcitePerformer = new ScalcitePerformer(calcite)
}