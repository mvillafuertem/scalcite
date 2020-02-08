package io.github.mvillafuertem.scalcite.example.api.documentation

import java.util.UUID

import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.{Json, JsonObject}
import io.github.mvillafuertem.scalcite.example.api.Query
import sttp.tapir._
import sttp.tapir.json.circe._

trait ScalciteEndpoint {

  // I N F O R M A T I O N
  private[api] lazy val queriesResource: String = "queries"
  private[api] lazy val simulateResource: String = "simulate"
  private[api] lazy val uuidPath = path[UUID]("uuid")
  private[api] lazy val queriesIdResource: EndpointInput[UUID] = queriesResource / uuidPath
  private[api] lazy val queriesIdSimulateResource: EndpointInput[UUID] = queriesIdResource / simulateResource
  private[api] lazy val queriesNamePostResource: String = "queries-post-resource"
  private[api] lazy val queriesNameGetResource: String = "queries-get-resource"
  private[api] lazy val simulateNameResource: String = "simulate-resource"
  private[api] lazy val queriesDescriptionPostResource: String = "Queries Post Endpoint"
  private[api] lazy val queriesDescriptionGetResource: String = "Queries Get Endpoint"
  private[api] lazy val simulateDescriptionResource: String = "Simulate Endpoint"

  private val queriesExample: Query = Query(value = "SELECT `personalinfo.address` FROM scalcite")

  // E N D P O I N T
  private[api] lazy val queriesPostEndpoint =
    ApiEndpoint.baseEndpoint.post
      .in(queriesResource)
      .name(queriesNamePostResource)
      .description(queriesDescriptionPostResource)
      .in(jsonBody[Query].example(queriesExample))
      .out(streamBody[Source[ByteString, Any]](schemaFor[Query], CodecFormat.Json()))
      .errorOut(statusCode)

  private[api] lazy val queriesGetEndpoint =
    ApiEndpoint.baseEndpoint.get
      .in(queriesIdResource)
      .name(queriesNameGetResource)
      .description(queriesDescriptionGetResource)
      .out(streamBody[Source[ByteString, Any]](schemaFor[Query], CodecFormat.Json()))
      .errorOut(statusCode)

  private[api] lazy val simulateEndpoint =
    ApiEndpoint.baseEndpoint.post
      .in(queriesIdSimulateResource)
      .name(simulateNameResource)
      .description(simulateDescriptionResource)
      .in(jsonBody[Json].example(Json.fromJsonObject(JsonObject.empty)))
      .out(streamBody[Source[ByteString, Any]](schemaFor[Json], CodecFormat.Json()))
      .errorOut(statusCode)

}

object ScalciteEndpoint extends ScalciteEndpoint
