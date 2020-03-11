package io.github.mvillafuertem.scalcite.example.domain

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import zio.stream

/**
 * @author Miguel Villafuerte
 */
trait ScalciteApplication {

  def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]]

  def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json]

  def createQuery(query: Query): stream.Stream[ScalciteError, Query]

  def deleteQueryByUUID(uuid: UUID): stream.Stream[Throwable, Int]

  def findQueryByUUID(uuid: UUID): stream.Stream[Throwable, Query]

  def findAll(): stream.Stream[Throwable, Query]

}
