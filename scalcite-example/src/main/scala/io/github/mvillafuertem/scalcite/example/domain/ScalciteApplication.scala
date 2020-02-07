package io.github.mvillafuertem.scalcite.example.domain

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import zio.stream

/**
 * @author Miguel Villafuerte
 */
trait ScalciteApplication {

  def performMap(uuid: UUID, map: collection.Map[String, Any]): stream.Stream[Throwable, collection.Map[String, Any]]

  def performJson(uuid: UUID, json: Json): stream.Stream[Throwable, collection.Map[String, Any]]

  def createQuery(query: Query): stream.Stream[Throwable, Query]

  def deleteQueryByUUID(uuid: UUID): stream.Stream[Throwable, Int]

  def findQueryByUUID(uuid: UUID): stream.Stream[Throwable, Query]

}
