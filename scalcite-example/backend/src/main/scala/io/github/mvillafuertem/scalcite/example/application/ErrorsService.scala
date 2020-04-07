package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.ErrorDBO
import zio.stream

final class ErrorsService(repository: ErrorsRepository[ErrorDBO]) extends ErrorsApplication {

  override def create(t: ScalciteError): stream.Stream[ScalciteError, ScalciteError] = ???

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] = ???

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, ScalciteError] = ???

  override def findAll(): stream.Stream[Throwable, ScalciteError] =
    repository.findAll().map(dbo => Unknown(dbo.value, dbo.uuid))


}

object ErrorsService {
  def apply(repository: ErrorsRepository[ErrorDBO]): ErrorsService = new ErrorsService(repository)
}
