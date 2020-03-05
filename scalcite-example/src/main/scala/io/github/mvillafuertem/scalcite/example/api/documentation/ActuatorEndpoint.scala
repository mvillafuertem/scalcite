package io.github.mvillafuertem.scalcite.example.api.documentation

import io.circe.Json
import io.circe.parser._
import io.github.mvillafuertem.scalcite.BuildInfo
import sttp.model.StatusCode
import sttp.tapir.Codec.JsonCodec
import sttp.tapir.json.circe._
import sttp.tapir.{Endpoint, jsonBody, _}

trait ActuatorEndpoint {

  // I N F O R M A T I O N
  type HealthInfo = Map[String, Any]
  private[api] lazy val healthResource: String = "health"
  private[api] lazy val healthNameResource: String = "health-resource"
  private[api] lazy val healthDescriptionResource: String = "Scalcite Service Health Check Endpoint"

  // E N D P O I N T
  private[api] lazy val healthEndpoint: Endpoint[Unit, StatusCode, HealthInfo, Nothing] =
    ApiEndpoint.baseEndpoint
      .get
      .in(healthResource)
      .name(healthNameResource)
      .description(healthDescriptionResource)
      .out(jsonBody[HealthInfo].example(BuildInfo.toMap))
      .errorOut(statusCode)

  // C O D E C
  private[api] implicit lazy val buildInfoCodec: JsonCodec[HealthInfo] =
    implicitly[JsonCodec[Json]].map(_ => BuildInfo.toMap)(_ => parse(BuildInfo.toJson) match {
      case Left(_) => throw new RuntimeException("health doesn't work")
      case Right(value) => value
    })

}

object ActuatorEndpoint extends ActuatorEndpoint