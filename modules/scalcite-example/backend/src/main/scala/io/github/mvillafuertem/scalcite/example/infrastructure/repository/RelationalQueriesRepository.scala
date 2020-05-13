package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.{ErrorDBO, QueryDBO}
import scalikejdbc._
import scalikejdbc.streams._
import zio.interop.reactivestreams._
import zio.stream.ZStream
import zio.{Has, Task, ULayer, ZLayer, stream}

import scala.concurrent.ExecutionContext

/**
 * @author Miguel Villafuerte
 */
private final class RelationalQueriesRepository(databaseName: String) extends QueriesRepository[QueryDBO] {

  implicit def executeOperation(sqlUpdateWithGeneratedKey: SQLUpdateWithGeneratedKey): stream.Stream[Throwable, Long] =
    ZStream.fromEffect(
      Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdateWithGeneratedKey.apply()))
    )

  implicit def executeUpdateOperation(sqlUpdate: SQLUpdate): stream.Stream[Throwable, Int] =
    ZStream.fromEffect(
      Task.effect(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sqlUpdate.apply()))
    )

  implicit def executeStreamOperation[T](streamReadySQL: StreamReadySQL[T])(implicit executionContext: ExecutionContext): stream.Stream[Throwable, T] =
    (NamedDB(Symbol(databaseName)) readOnlyStream streamReadySQL).toStream()

  implicit def executeSQLOperation[T](sql: SQL[T, HasExtractor]): stream.Stream[Throwable, T] =
    ZStream.fromIterable(NamedDB(Symbol(databaseName)).autoCommit(implicit session => sql.list().apply()))

  private def queryCreate(queryDBO: QueryDBO): SQL[Nothing, NoExtractor] =
    sql"INSERT INTO QUERIES(UUID, VALUE) VALUES (${queryDBO.uuid}, ${queryDBO.value})"

  override def insert(dbo: QueryDBO): stream.Stream[Throwable, Long] =
    queryCreate(dbo).updateAndReturnGeneratedKey()

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    queryDeleteByUUID(QueryDBO.table, uuid).update()

  override def findById(id: Long): stream.Stream[Throwable, QueryDBO] =
    queryFindById(QueryDBO.table, id)
      .map(rs => QueryDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()
  override def findByUUID(uuid: UUID): stream.Stream[Throwable, QueryDBO] =
    queryFindByUUID(QueryDBO.table, uuid)
      .map(rs => QueryDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()

  override def findAll(): stream.Stream[Throwable, QueryDBO] =
    queryFindAll(QueryDBO.table)
      .map(rs => QueryDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()

}
object RelationalQueriesRepository {

  def apply(databaseName: String): QueriesRepository[QueryDBO] =
    new RelationalQueriesRepository(databaseName)

  type ZQueriesRepository = Has[QueriesRepository[QueryDBO]]

  def findByUUID(uuid: UUID): stream.ZStream[ZQueriesRepository, Throwable, QueryDBO] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  def findAll(): stream.ZStream[ZQueriesRepository, Throwable, QueryDBO] =
    stream.ZStream.accessStream(_.get.findAll())

  def findById(id: Long): stream.ZStream[ZQueriesRepository, Throwable, QueryDBO] =
    stream.ZStream.accessStream(_.get.findById(id))

  def insert(query: QueryDBO): stream.ZStream[ZQueriesRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.insert(query))

  def deleteByUUID(uuid: UUID): stream.ZStream[ZQueriesRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteByUUID(uuid))

  val live: ZLayer[Has[String], Nothing, ZQueriesRepository] =
    ZLayer.fromService[String, QueriesRepository[QueryDBO]](databaseName => RelationalQueriesRepository(databaseName))

  def make(databaseName: String): ULayer[ZQueriesRepository] =
    ZLayer.succeed(databaseName) >>> live

}
