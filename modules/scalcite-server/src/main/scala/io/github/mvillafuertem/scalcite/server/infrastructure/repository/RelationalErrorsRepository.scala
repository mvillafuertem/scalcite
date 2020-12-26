package io.github.mvillafuertem.scalcite.server.infrastructure.repository

import io.github.mvillafuertem.scalcite.server.domain.repository.ErrorsRepository

import java.util.UUID
import io.github.mvillafuertem.scalcite.server.infrastructure.model.ErrorDBO
import scalikejdbc.streams.{StreamReadySQL, _}
import scalikejdbc.{HasExtractor, NamedDB, NoExtractor, SQL, SQLUpdate, SQLUpdateWithGeneratedKey, _}
import zio.interop.reactivestreams._
import zio.stream.ZStream
import zio.{Has, Task, ULayer, ZLayer, stream}

import scala.concurrent.ExecutionContext

private final class RelationalErrorsRepository(databaseName: String) extends ErrorsRepository[ErrorDBO] {

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

  private def queryCreate(queryDBO: ErrorDBO): SQL[Nothing, NoExtractor] =
    sql"INSERT INTO ERRORS(UUID, VALUE, DATE) VALUES (${queryDBO.uuid}, ${queryDBO.code}, ${queryDBO.timestamp})"

  override def insert(dbo: ErrorDBO): stream.Stream[Throwable, Long] =
    queryCreate(dbo).updateAndReturnGeneratedKey()

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    queryDeleteByUUID(ErrorDBO.table, uuid).update()

  override def findById(id: Long): stream.Stream[Throwable, ErrorDBO]     =
    queryFindById(ErrorDBO.table, id)
      .map(rs => ErrorDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()
  override def findByUUID(uuid: UUID): stream.Stream[Throwable, ErrorDBO] =
    queryFindByUUID(ErrorDBO.table, uuid)
      .map(rs => ErrorDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()

  override def findAll(): stream.Stream[Throwable, ErrorDBO] =
    queryFindAll(ErrorDBO.table)
      .map(rs => ErrorDBO(rs))
  // https://github.com/scalikejdbc/scalikejdbc/issues/1050
  //.iterator()

}

object RelationalErrorsRepository {

  def apply(databaseName: String): ErrorsRepository[ErrorDBO] =
    new RelationalErrorsRepository(databaseName)

  type ZErrorsRepository = Has[ErrorsRepository[ErrorDBO]]

  def findByUUID(uuid: UUID): stream.ZStream[ZErrorsRepository, Throwable, ErrorDBO] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  def findAll(): stream.ZStream[ZErrorsRepository, Throwable, ErrorDBO] =
    stream.ZStream.accessStream(_.get.findAll())

  def findById(id: Long): stream.ZStream[ZErrorsRepository, Throwable, ErrorDBO] =
    stream.ZStream.accessStream(_.get.findById(id))

  def insert(query: ErrorDBO): stream.ZStream[ZErrorsRepository, Throwable, Long] =
    stream.ZStream.accessStream(_.get.insert(query))

  def deleteByUUID(uuid: UUID): stream.ZStream[ZErrorsRepository, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteByUUID(uuid))

  val live: ZLayer[Has[String], Nothing, ZErrorsRepository] =
    ZLayer.fromService[String, ErrorsRepository[ErrorDBO]](RelationalErrorsRepository(_))

  def make(databaseName: String): ULayer[ZErrorsRepository] =
    ZLayer.succeed(databaseName) >>> live

}
