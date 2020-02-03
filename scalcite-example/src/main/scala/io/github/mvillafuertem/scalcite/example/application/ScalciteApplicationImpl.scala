package io.github.mvillafuertem.scalcite.example.application

import akka.stream.scaladsl.{Flow, Source}
import io.circe.scalcite.blower.ScalciteBlower
import io.circe.scalcite.flattener.ScalciteFlattener
import io.github.mvillafuertem.scalcite.blower.core.JsonBlower
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.repository.{ScalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.flattener.core.{JsonFlattener, JsonParser}

/**
  * @author Miguel Villafuerte
  */
final class ScalciteApplicationImpl(sqlRepository: QueriesRepository[Source],
                                    scalciteRepository: ScalciteRepository[Source]) extends ScalciteApplication[Source] {


  def perform(id: Long, json: String): Source[String, _] = {

    val one: Source[Map[String, Any], _] = sqlRepository.findById(id)
    val two: Source[Map[String, Any], _] = one.flatMapConcat(sql => scalciteRepository.queryForMap(new JsonFlattener toMap json, String.valueOf(sql("sql"))))
    val three: Source[String, _] = two.via(Flow.fromFunction(JsonParser.parse))
    val four: Source[String, _] = three.via(Flow.fromFunction(new JsonBlower().toJsonString))
    four
  }


  def performJson(id: Long, json: String): Source[String, _] = {

    val one: Source[Map[String, Any], _] = sqlRepository.findById(id)
    val two: Source[Map[String, Any], _] = one.flatMapConcat(sql => {
      var flattenedJson = ScalciteFlattener.flatten(json) match {
        case Left(value) => throw value
        case Right(value) => value
      }
      val value = scalciteRepository.queryForJson(flattenedJson, String.valueOf(sql("sql")))
      flattenedJson = null
      value
    })
    val three: Source[String, _] = two.via(Flow.fromFunction(JsonParser.parse))
    val four: Source[String, _] = three.via(Flow.fromFunction(a => ScalciteBlower.blow(a) match {
      case Left(value) => throw value
      case Right(value) => value.noSpaces
    }))
    four
  }

}

object ScalciteApplicationImpl {
  def apply(sqlRepository: QueriesRepository[Source],
            scalciteRepository: ScalciteRepository[Source]): ScalciteApplicationImpl = new ScalciteApplicationImpl(sqlRepository, scalciteRepository)
}
