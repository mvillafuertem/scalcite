package io.github.mvillafuertem.scalcite.example.application

import java.sql.SQLException
import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.QueriesApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.{DuplicatedEntity, Unknown}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.{ErrorsRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.model.{ErrorDBO, QueryDBO}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.ZQueriesRepository
import zio.stream.ZStream
import zio.{Has, URLayer, ZLayer, stream}

final class QueriesService(repository: QueriesRepository[QueryDBO],
                           errorsRepository: ErrorsRepository[ErrorDBO]) extends QueriesApplication {
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
    }.catchAll(catchErrorAndSaveInDB)

  private def catchErrorAndSaveInDB: ScalciteError => stream.Stream[ScalciteError, Query] =
    error =>
      errorsRepository.insert(ErrorDBO(error.uuid, error.code, error.timestamp))
        .mapError { case e: SQLException => Unknown(e.getMessage) } *>
        ZStream.fail(error)

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    repository.deleteByUUID(uuid)

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, Query] =
    repository.findByUUID(uuid).map(dbo => Query(dbo.uuid, dbo.value))


  override def findAll(): stream.Stream[Throwable, Query] =
    repository.findAll().map(dbo => Query(dbo.uuid, dbo.value))
}

object QueriesService {

  def apply(repository: QueriesRepository[QueryDBO], errorsRepository: ErrorsRepository[ErrorDBO]): QueriesService =
    new QueriesService(repository, errorsRepository)

  type ZQueriesApplication = Has[QueriesApplication]

  def create(query: Query): stream.ZStream[ZQueriesApplication, ScalciteError, Query] =
    stream.ZStream.accessStream(_.get.create(query))

  def deleteByUUID(uuid: UUID): stream.ZStream[ZQueriesApplication, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteByUUID(uuid))

  def findAll(): stream.ZStream[ZQueriesApplication, Throwable, Query] =
    stream.ZStream.accessStream(_.get.findAll())

  def findByUUID(uuid: UUID): stream.ZStream[ZQueriesApplication, Throwable, Query] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  val live: URLayer[ZQueriesRepository with ZErrorsRepository, ZQueriesApplication] =
    ZLayer.fromServices[QueriesRepository[QueryDBO], ErrorsRepository[ErrorDBO], QueriesApplication](
      QueriesService.apply)

}
