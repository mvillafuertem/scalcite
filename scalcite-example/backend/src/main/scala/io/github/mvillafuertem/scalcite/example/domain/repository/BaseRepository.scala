package io.github.mvillafuertem.scalcite.example.domain.repository

import java.util.UUID

import zio.stream

trait BaseRepository[T] {

  def insert(dbo: T): stream.Stream[Throwable, Long]

  def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int]

  def findById(id: Long): stream.Stream[Throwable, T]

  def findByUUID(uuid: UUID): stream.Stream[Throwable, T]

  def findAll(): stream.Stream[Throwable, T]

}
