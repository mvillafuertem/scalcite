package io.github.mvillafuertem.scalcite.example.application

import java.sql.SQLException
import java.util.UUID

import io.github.mvillafuertem.scalcite.example.domain.QueriesApplication
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.{DuplicatedEntity, Unknown}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.QueriesRepo
import zio.{Has, ZLayer, stream}


object QueriesService {

  type QueriesApp = Has[QueriesApplication]

  def create(query: Query): stream.ZStream[QueriesApp, ScalciteError, Query] =
    stream.ZStream.accessStream(_.get.create(query))

  def deleteByUUID(uuid: UUID): stream.ZStream[QueriesApp, Throwable, Int] =
    stream.ZStream.accessStream(_.get.deleteByUUID(uuid))

  def findAll(): stream.ZStream[QueriesApp, Throwable, Query] =
    stream.ZStream.accessStream(_.get.findAll())

  def findByUUID(uuid: UUID): stream.ZStream[QueriesApp, Throwable, Query] =
    stream.ZStream.accessStream(_.get.findByUUID(uuid))

  val live: ZLayer[QueriesRepo, Nothing, QueriesApp] = ZLayer.fromFunction(repository => new QueriesApplication {
    override def create(query: Query): stream.Stream[ScalciteError, Query] =
      (for {
        input <- stream.Stream(QueryDBO(query.uuid, query.value))
        stream <- repository.get.insert(input).map{
          case r@_ if r > 0 => query
          case _  => query.copy(value = ScalciteError.Unknown.toString)
        }
      } yield stream).mapError {
        case e: SQLException => e.getSQLState match {
          case "23505" => DuplicatedEntity()
          case _ => Unknown()
        }
      }

    override def deleteByUUID(uuid: UUID): stream.Stream[Throwable, Int] =
      repository.get.deleteByUUID(uuid)

    override def findByUUID(uuid: UUID): stream.Stream[Throwable, Query] =
      repository.get.findByUUID(uuid).map(dbo => Query(dbo.uuid, dbo.value))


    override def findAll(): stream.Stream[Throwable, Query] =
      repository.get.findAll().map(dbo => Query(dbo.uuid, dbo.value))

  })

}
