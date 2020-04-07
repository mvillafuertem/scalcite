package io.github.mvillafuertem.scalcite.example.domain.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import zio.stream

trait ErrorsRepository {

  def insert(queryDBO: QueryDBO): stream.Stream[Throwable, Long]

  def findById(id: Long): stream.Stream[Throwable, QueryDBO]

  def findByUUID(uuid: UUID): stream.Stream[Throwable, QueryDBO]

  def findAll(): stream.Stream[Throwable, QueryDBO]

}
