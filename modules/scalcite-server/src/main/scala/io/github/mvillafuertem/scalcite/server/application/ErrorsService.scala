package io.github.mvillafuertem.scalcite.server.application

import io.github.mvillafuertem.scalcite.server.domain.ErrorsApplication

import java.util.UUID
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.server.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.server.infrastructure.model.ErrorDBO
import zio.{Has, URLayer, ZLayer, stream}

private final class ErrorsService(repository: ErrorsRepository[ErrorDBO]) extends ErrorsApplication {

  override def create(t: ScalciteError): stream.Stream[ScalciteError, ScalciteError] =
    zio.stream.Stream.fail(Unknown())

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    zio.stream.Stream.fail(new RuntimeException())

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, ScalciteError] =
    zio.stream.Stream.fail(new RuntimeException())

  override def findAll(): stream.Stream[Throwable, ScalciteError] =
    repository.findAll().map(dbo => Unknown(dbo.code, dbo.uuid))

}

object ErrorsService {

  def apply(repository: ErrorsRepository[ErrorDBO]): ErrorsApplication =
    new ErrorsService(repository)

  type ZErrorsApplication = Has[ErrorsApplication]

  def findAll(): stream.ZStream[ZErrorsApplication, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findAll())

  def findByUUID(uuid: UUID): stream.ZStream[ZErrorsApplication, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  val live: URLayer[ZErrorsRepository, ZErrorsApplication] =
    ZLayer.fromService[ErrorsRepository[ErrorDBO], ErrorsApplication](ErrorsService.apply)

}
