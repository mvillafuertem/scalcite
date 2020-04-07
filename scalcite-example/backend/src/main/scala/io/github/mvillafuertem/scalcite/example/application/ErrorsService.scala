package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication
import io.github.mvillafuertem.scalcite.example.domain.ErrorsApplication.ErrorsApp
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.domain.repository.ErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.ErrorDBO
import zio.{ZLayer, stream}

object ErrorsService {

  def findAll(): stream.ZStream[ErrorsApp, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findAll())

  def findByUUID(uuid: UUID): stream.ZStream[ErrorsApp, Throwable, ScalciteError] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))


  val live: ZLayer[ErrorsRepository[ErrorDBO], Nothing, ErrorsApp] = ZLayer.fromFunction(repository => new ErrorsApplication {
    override def create(t: ScalciteError): stream.ZStream[ErrorsApp, ScalciteError, ScalciteError] =
      zio.stream.Stream.fail(Unknown())

    override def deleteByUUID(uuid: UUID): stream.ZStream[ErrorsApp, Throwable, Int] =
      zio.stream.Stream.fail(new RuntimeException())

    override def findByUUID(uuid: UUID): stream.ZStream[ErrorsApp, Throwable, ScalciteError] =
      zio.stream.Stream.fail(new RuntimeException())

    override def findAll(): stream.ZStream[ErrorsApp, Throwable, ScalciteError] =
      repository.findAll().map(dbo => Unknown(dbo.value, dbo.uuid))

  })

}
