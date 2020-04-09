package io.github.mvillafuertem.scalcite.example.api.documentation

import io.circe.generic.extras.Configuration
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._

trait ApiJsonCodec {

  implicit val customConfig: Configuration = Configuration.default.withDefaults

  private[api] implicit lazy val stringCodec: JsonCodec[String] =
    implicitly[JsonCodec[Json]].map(json => json.noSpaces)(string => parse(string) match {
      case Left(_) => throw new RuntimeException("ApiJsonCoded")
      case Right(value) => value
    })

  // S C A L C I T E  E R R O R  C O D E C
  private[api] implicit def scalciteErrorCodec[A <: ScalciteError]: JsonCodec[A] =
    implicitly[JsonCodec[Json]].map(json => json.as[A] match {
      case Left(_) => throw new RuntimeException("MessageParsingError")
      case Right(value) => value
    })(error => error.asJson)

  implicit def encodeScalciteError[A <: ScalciteError]: Encoder[A] =
    (e: A) => Json.obj( ("error", Json.obj(
      ("uuid", Json.fromString(e.uuid.toString)),
      ("code", Json.fromString(e.code)),
      ("timestamp", Json.fromString(e.timestamp.toString))
    )))

  implicit def decodeScalciteError[A <: ScalciteError]: Decoder[A] = (c: HCursor) => for {
    error <- c.get[String]("code")
  } yield Unknown(error).asInstanceOf[A]


}

object ApiJsonCodec extends ApiJsonCodec
