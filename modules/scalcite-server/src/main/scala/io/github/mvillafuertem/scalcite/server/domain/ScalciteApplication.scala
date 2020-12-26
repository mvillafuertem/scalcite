package io.github.mvillafuertem.scalcite.server.domain

import java.util.UUID

import io.circe.Json
import zio.stream

/**
 * @author Miguel Villafuerte
 */
trait ScalciteApplication {

  def performMap(map: collection.Map[String, Any], uuid: UUID*): stream.Stream[Throwable, collection.Map[String, Any]]

  def performJson(json: Json, uuid: UUID*): stream.Stream[Throwable, Json]

}
