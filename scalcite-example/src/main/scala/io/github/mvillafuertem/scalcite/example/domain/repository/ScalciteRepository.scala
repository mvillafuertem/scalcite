package io.github.mvillafuertem.scalcite.example.domain.repository

import io.circe.Json

/**
  * @author Miguel Villafuerte
  */
trait ScalciteRepository[F[_,_]] {

  def queryForMap(map: Map[String, Any], sql: String): F[Map[String, Any], _]

  def queryForJson(json: Json, sql: String): F[Map[String, Any], _]

}
