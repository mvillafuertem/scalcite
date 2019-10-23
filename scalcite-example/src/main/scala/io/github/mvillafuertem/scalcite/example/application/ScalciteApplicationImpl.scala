package io.github.mvillafuertem.scalcite.example.application

import akka.stream.scaladsl.{Flow, Source}
import io.github.mvillafuertem.blower.{JsonBlower, JsonFlattener, JsonParser}
import io.github.mvillafuertem.scalcite.example.domain.ScalciteApplication
import io.github.mvillafuertem.scalcite.example.domain.repository.{ScalciteRepository, SqlRepository}

/**
  * @author Miguel Villafuerte
  */
final class ScalciteApplicationImpl(sqlRepository: SqlRepository[Source],
                                     scalciteRepository: ScalciteRepository[Source]) extends ScalciteApplication[Source] {


  def perform(id: Long, json: String): Source[String, _] = {

    val one: Source[Map[String, Any], _] = sqlRepository.findById(id)
    val two: Source[Map[String, Any], _] = one.flatMapConcat(sql => scalciteRepository.queryForMap(new JsonFlattener toMap json, String.valueOf(sql("sql"))))
    val three: Source[String, _] = two.via(Flow.fromFunction(JsonParser.parse))
    val four: Source[String, _] = three.via(Flow.fromFunction(new JsonBlower().toJsonString))
    four
  }

}

object ScalciteApplicationImpl {
  def apply(sqlRepository: SqlRepository[Source],
            scalciteRepository: ScalciteRepository[Source]): ScalciteApplicationImpl = new ScalciteApplicationImpl(sqlRepository, scalciteRepository)
}
