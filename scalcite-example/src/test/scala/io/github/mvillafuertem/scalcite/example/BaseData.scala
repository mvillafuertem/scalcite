package io.github.mvillafuertem.scalcite.example

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO

trait BaseData {

  val map: collection.Map[String, Any] = collection.Map[String, Any]("boolean" -> true)
  val json: Json = Json.obj("boolean" -> Json.fromBoolean(true))

  val queryBooleanValue = "SELECT `boolean` FROM scalcite"
  val queryStringValue = "SELECT 'favoriteFruit' FROM scalcite"

  val id = 0L

  val uuid1: UUID = UUID.randomUUID()
  val uuid2: UUID = UUID.randomUUID()

  val query: Query = Query(uuid1, queryStringValue)
  val queryDBO1: QueryDBO = QueryDBO(uuid1,queryStringValue)
  val queryDBO2: QueryDBO = QueryDBO(uuid2, queryStringValue)

}
