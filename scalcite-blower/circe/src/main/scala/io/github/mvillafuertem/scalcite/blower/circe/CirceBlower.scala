package io.github.mvillafuertem.scalcite.blower.circe

import io.circe.{Json, ParsingFailure}
import io.github.mvillafuertem.scalcite.blower.core.{Blower, JsonParser, MapBlower}

/**
 * @author Miguel Villafuerte
 */
object CirceBlower {

  implicit val circeBlower: Blower[Json, Either[ParsingFailure, Json]] = (json: Json) => {
    val parsedJson = JsonParser parse json.noSpaces
    val str = JsonParser parse MapBlower.apply(parsedJson).blowUp
    io.circe.jawn.parse(str)
  }
}
