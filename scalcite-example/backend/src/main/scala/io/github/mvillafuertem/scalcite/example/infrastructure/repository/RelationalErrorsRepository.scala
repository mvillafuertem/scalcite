package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.ErrorDBO
import zio.stream

final class RelationalErrorsRepository extends ErrorsRepository[ErrorDBO] {

  override def insert(dbo: ErrorDBO): stream.Stream[Throwable, Long] = ???

  override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] = ???

  override def findById(id: Long): stream.Stream[Throwable, ErrorDBO] = ???

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, ErrorDBO] = ???

  override def findAll(): stream.Stream[Throwable, ErrorDBO] = ???

}

object RelationalErrorsRepository {
  def apply(): RelationalErrorsRepository = new RelationalErrorsRepository()
}
