package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import scalikejdbc._
import scalikejdbc.streams._
import zio.interop.reactiveStreams._
import zio.stream
import zio.stream.ZStream

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
final class RelationalQueriesRepository(databaseName: String)(
    implicit executionContext: ExecutionContext
) extends QueriesRepository {

  implicit def executeOperation(sqlUpdateWithGeneratedKey: SQLUpdateWithGeneratedKey): stream.Stream[Throwable, Long] =
    ZStream(NamedDB(Symbol(databaseName)).autoCommit{implicit session => sqlUpdateWithGeneratedKey.apply()})

  implicit def executeUpdateOperation(sqlUpdate: SQLUpdate): stream.Stream[Throwable, Int] =
    ZStream(NamedDB(Symbol(databaseName)).autoCommit{implicit session => sqlUpdate.apply()})

  implicit def executeStreamOperation[T](streamReadySQL: StreamReadySQL[T]): stream.Stream[Throwable, T] =
    (NamedDB(Symbol(databaseName)) readOnlyStream streamReadySQL).toStream()

  private def queryFindById(id: Long): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM QUERIES WHERE ID = $id"

  private def queryFindByUUID(uuid: UUID): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM QUERIES WHERE UUID = $uuid"

  private def queryCreate(queryDBO: QueryDBO): SQL[Nothing, NoExtractor] =
    sql"INSERT INTO QUERIES(UUID, VALUE) VALUES (${queryDBO.uuid}, ${queryDBO.value})"

  private def queryDeleteByUUID(uuid: UUID): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM QUERIES WHERE UUID = $uuid"


  override def findByUUID(uuid: UUID): stream.Stream[Throwable, QueryDBO] =
    queryFindByUUID(uuid)
      .map(rs => QueryDBO(rs))
      .iterator()

  override def findById(id: Long): stream.Stream[Throwable, QueryDBO] =
    queryFindById(id)
      .map(rs => QueryDBO(rs))
      .iterator()

  override def insert(query: QueryDBO): stream.Stream[Throwable, Long] =
    queryCreate(query).updateAndReturnGeneratedKey()

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    queryDeleteByUUID(uuid).update()
}

object RelationalQueriesRepository {
  def apply(databaseName: String)(implicit executionContext: ExecutionContext): RelationalQueriesRepository = new RelationalQueriesRepository(databaseName)(executionContext)
}