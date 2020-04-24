package io.github.mvillafuertem.scalcite.example.domain.repository

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import zio.stream

/**
 * @author Miguel Villafuerte
 */
trait CalciteRepository {

  def queryForMap(map: collection.Map[String, Any], sql: String): stream.Stream[ScalciteError, collection.Map[String, Any]]

  def queryForJson(json: Json, sql: String): stream.Stream[ScalciteError, Json]

}
