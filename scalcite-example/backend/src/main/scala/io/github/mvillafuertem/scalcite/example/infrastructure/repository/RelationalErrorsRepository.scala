package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import zio.stream

final class RelationalErrorsRepository extends ErrorsRepository {

  override def insert(queryDBO: QueryDBO): stream.Stream[Throwable, Long] = ???

  override def findById(id: Long): stream.Stream[Throwable, QueryDBO] = ???

  override def findByUUID(uuid: UUID): stream.Stream[Throwable, QueryDBO] = ???

  override def findAll(): stream.Stream[Throwable, QueryDBO] = ???

}
