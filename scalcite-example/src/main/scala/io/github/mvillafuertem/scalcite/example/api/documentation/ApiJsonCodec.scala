package io.github.mvillafuertem.scalcite.example.api.documentation

import io.circe.Json
import io.circe.parser.parse
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._

trait ApiJsonCodec {

  private[api] implicit lazy val stringCodec: JsonCodec[String] =
    implicitly[JsonCodec[Json]].map(json => json.noSpaces)(string => parse(string) match {
      case Left(_) => throw new RuntimeException("ApiJsonCoded")
      case Right(value) => value
    })

}
