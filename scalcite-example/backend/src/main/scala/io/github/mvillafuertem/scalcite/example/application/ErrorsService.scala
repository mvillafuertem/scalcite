package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.ErrorDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalErrorsRepository.ErrorsRepo
import zio.{Has, URLayer, ZLayer, stream}

private final class ErrorsService(repository: ErrorsRepository[ErrorDBO]) extends ErrorsApplication {

  override def create(t: ScalciteError): stream.Stream[ScalciteError, ScalciteError] =
    zio.stream.Stream.fail(Unknown())

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
    zio.stream.Stream.fail(new RuntimeException())

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, ScalciteError] =
    zio.stream.Stream.fail(new RuntimeException())

  override def findAll(): stream.Stream[Throwable, ScalciteError] =
    repository.findAll().map(dbo => Unknown(dbo.value, dbo.uuid))

}

object ErrorsService {

  def apply(repository: ErrorsRepository[ErrorDBO]): ErrorsApplication =
    new ErrorsService(repository)

  type ZErrorsApplication = Has[ErrorsApplication]

  def findAll(): stream.ZStream[ZErrorsApplication, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findAll())

  def findByUUID(uuid: UUID): stream.ZStream[ZErrorsApplication, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  val live: URLayer[ErrorsRepo, ZErrorsApplication] =
    ZLayer.fromService[ErrorsRepository[ErrorDBO], ErrorsApplication](
      repository => ErrorsService(repository))

}
