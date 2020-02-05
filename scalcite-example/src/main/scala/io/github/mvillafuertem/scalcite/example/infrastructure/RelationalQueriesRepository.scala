package io.github.mvillafuertem.scalcite.example.infrastructure

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.configuration.properties.H2ConfigurationProperties
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.RelationalQueriesRepository.TypeZStream
import scalikejdbc.streams._
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, _}
import zio.interop.reactiveStreams._
import zio.stream.ZStream

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
final class RelationalQueriesRepository(h2ConfigurationProperties: H2ConfigurationProperties)(
    implicit executionContext: ExecutionContext
) extends QueriesRepository[TypeZStream] {

  ConnectionPool.add(Symbol("sqldb"),
    h2ConfigurationProperties.url,
    h2ConfigurationProperties.user,
    h2ConfigurationProperties.password,
    ConnectionPoolSettings(
      initialSize = h2ConfigurationProperties.initialSize,
      maxSize = h2ConfigurationProperties.maxSize,
      connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
      validationQuery = h2ConfigurationProperties.validationQuery,
      driverName = h2ConfigurationProperties.driverName
    )
  )

  implicit def executeOperation(sqlUpdateWithGeneratedKey: SQLUpdateWithGeneratedKey): ZStream[Any, Throwable, Long] =
    ZStream(NamedDB(Symbol("sqldb")).autoCommit{implicit session => sqlUpdateWithGeneratedKey.apply()})

  implicit def executeUpdateOperation(sqlUpdate: SQLUpdate): ZStream[Any, Throwable, Int] =
    ZStream(NamedDB(Symbol("sqldb")).autoCommit{implicit session => sqlUpdate.apply()})

  implicit def executeStreamOperation[T](streamReadySQL: StreamReadySQL[T]): ZStream[Any, Throwable, T] =
    NamedDB(Symbol("sqldb")).readOnlyStream(streamReadySQL).toStream()

  private def queryFindById(id: Long): SQL[Nothing, NoExtractor] =
    sql"SELECT * FROM QUERIES WHERE ID = $id"

  private def queryCreate(queryDBO: QueryDBO): SQL[Nothing, NoExtractor] =
    sql"INSERT INTO QUERIES(UUID, VALUE) VALUES (${queryDBO.uuid}, ${queryDBO.value})"

  private def queryDelete(uuid: UUID): SQL[Nothing, NoExtractor] =
    sql"DELETE FROM QUERIES WHERE UUID = $uuid"


  override def findById(id: Long): ZStream[Any, Throwable, QueryDBO] =
    queryFindById(id)
      .map(rs => QueryDBO(rs))
      .iterator()

  override def insert(query: QueryDBO): ZStream[Any, Throwable, Long] =
    queryCreate(query).updateAndReturnGeneratedKey()

  override def delete(uuid: UUID): ZStream[Any, Throwable, Int] =
    queryDelete(uuid).update()
}

object RelationalQueriesRepository {

  type TypeZStream[A] = ZStream[_, _, _]

}
