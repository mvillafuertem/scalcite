package io.github.mvillafuertem.scalcite.flattener.circe

import io.circe.{Json, ParsingFailure}
import io.github.mvillafuertem.scalcite.flattener.core.{Flattener, JsonParser, MapFlattener}

/**
 * @author Miguel Villafuerte
 */
object CirceFlattener {

  implicit val circeFlattener: Flattener[Json, Either[ParsingFailure, Json]] = (json: Json) => {
    val parsedJson = JsonParser parse json.noSpaces
    val str = JsonParser parse MapFlattener.apply(parsedJson).flatten
    io.circe.jawn.parse(str)
  }

}
