package io.github.mvillafuertem.blower

import io.circe.{Json, ParsingFailure}

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
