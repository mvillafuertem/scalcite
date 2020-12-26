package io.github.mvillafuertem.scalcite.server.domain

import java.util.UUID
import io.github.mvillafuertem.scalcite.server.domain.error.ScalciteError
import zio.stream

trait BaseApplication[T] {

  def create(t: T): stream.Stream[ScalciteError, T]

  def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int]

  def findByUUID(uuid: UUID): stream.Stream[Throwable, T]

  def findAll(): stream.Stream[Throwable, T]

}
