package io.github.mvillafuertem.scalcite.example.domain

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import zio.stream

trait BaseApplication[R, T] {

  def create(t: T): stream.ZStream[R, ScalciteError, T]

  def deleteByUUID(uuid: UUID): stream.ZStream[R, Throwable, Int]

  def findByUUID(uuid: UUID): stream.ZStream[R, Throwable, T]

  def findAll(): stream.ZStream[R, Throwable, T]

}
