package io.github.mvillafuertem.scalcite.example.application

import java.sql.SQLException
import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.QueriesApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.{DuplicatedEntity, Unknown}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import zio.stream

final class QueriesService(repository: QueriesRepository[QueryDBO]) extends QueriesApplication {

  override def create(query: Query): stream.Stream[ScalciteError, Query] =
    (for {
      input <- stream.Stream(QueryDBO(query.uuid, query.value))
      stream <- repository.insert(input).map{
        case r@_ if r > 0 => query
        case _  => query.copy(value = ScalciteError.Unknown.toString)
      }
    } yield stream).mapError {
      case e: SQLException => e.getSQLState match {
        case "23505" => DuplicatedEntity()
        case _ => Unknown()
      }
    }

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    repository.deleteByUUID(uuid)

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, Query] =
    repository.findByUUID(uuid).map(dbo => Query(dbo.uuid, dbo.value))

  override def findAll(): stream.Stream[Throwable, Query] =
    repository.findAll().map(dbo => Query(dbo.uuid, dbo.value))

}

object QueriesService {
  def apply(repository: QueriesRepository[QueryDBO]): QueriesService = new QueriesService(repository)
}
