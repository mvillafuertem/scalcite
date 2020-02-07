package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import zio.stream

final class ScalcitePerformer(calcite: CalciteRepository, repository: QueriesRepository) extends ScalciteApplication {

  override def createQuery(query: Query): stream.Stream[Throwable, Query] =
    for {
      input <- stream.Stream(QueryDBO(query.uuid, query.value))
      stream <- repository.insert(input).map{
        case r@_ if r > 0 => query
        case _  => throw new RuntimeException("Domain Error")
      }
    } yield stream

  override def deleteQueryByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    repository.deleteByUUID(uuid)

  override def findQueryByUUID(uuid: UUID): stream.Stream[Throwable, Query] =
    repository.findByUUID(uuid).map(dbo => Query(dbo.uuid, dbo.value))

  override def performMap(uuid: UUID, map: collection.Map[String, Any]): stream.Stream[Throwable, collection.Map[String, Any]] =
    for {
      query <- findQueryByUUID(uuid)
      effect <- calcite.queryForMap(map, query.value)
    } yield effect

  override def performJson(uuid: UUID, json: Json): stream.Stream[Throwable, collection.Map[String, Any]] =
    for {
      query <- findQueryByUUID(uuid)
      effect <- calcite.queryForJson(json, query.value)
    } yield effect
}

object ScalcitePerformer {
  def apply(calcite: CalciteRepository, repository: QueriesRepository): ScalcitePerformer = new ScalcitePerformer(calcite, repository)
}