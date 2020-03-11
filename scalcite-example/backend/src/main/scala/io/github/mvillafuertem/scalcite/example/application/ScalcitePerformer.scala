package io.github.mvillafuertem.scalcite.example.application

import java.sql.SQLException
import java.util.UUID

import io.circe.Json
import io.circe.scalcite.blower.ScalciteBlower._
import io.circe.scalcite.flattener.ScalciteFlattener._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.blower.Blower._
import io.github.mvillafuertem.scalcite.example.api.documentation.ApiJsonCodec._
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.{DuplicatedEntity, Unknown}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.flattener.Flattener._
import zio.stream.ZStream
import zio.{IO, UIO, stream}


final class ScalcitePerformer(calcite: CalciteRepository, repository: QueriesRepository) extends ScalciteApplication {

  override def createQuery(query: Query): stream.Stream[ScalciteError, Query] =
    (for {
      input <- stream.Stream(QueryDBO(query.uuid, query.value))
      stream <- repository.insert(input).map{
        case r@_ if r > 0 => query
        case _  => query.copy(value = ScalciteError.Unknown.toString)
      }
    } yield stream).mapError {
        case e: SQLException => e.getSQLState match {
          case "23505" => DuplicatedEntity
          case _ => Unknown()
        }
    }


  override def deleteQueryByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    repository.deleteByUUID(uuid)

  override def findQueryByUUID(uuid: UUID): stream.Stream[Throwable, Query] =
    repository.findByUUID(uuid).map(dbo => Query(dbo.uuid, dbo.value))

  override def findAll(): stream.Stream[Throwable, Query] =
    repository.findAll().map(dbo => Query(dbo.uuid, dbo.value))

  override def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]] =
    ZStream.fromIterable(uuid)
        .flatMapPar(10)(uuid => findQueryByUUID(uuid))
        .flatMapPar(10)(query => calcite.queryForMap(map, query.value))

  override def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json] = {

    (for {
      flattened <- flattener(json)
      result <- ZStream.fromIterable(uuid)
        .flatMapPar(1)(uuid => findQueryByUUID(uuid).tap(_ => UIO(Thread.sleep(1000))))
        .flatMapPar(1)(query => performQuery(flattened, query.value))
      blowed <- blower(result)
    } yield blowed)}


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
  def apply(calcite: CalciteRepository, repository: QueriesRepository): ScalcitePerformer = new ScalcitePerformer(calcite, repository)
}