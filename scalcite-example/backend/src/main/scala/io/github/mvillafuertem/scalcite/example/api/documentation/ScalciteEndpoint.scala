package io.github.mvillafuertem.scalcite.example.api.documentation

import java.util.UUID

import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.Json
import io.circe.generic.auto._
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import sttp.tapir._
import sttp.tapir.json.circe._


trait ScalciteEndpoint extends ApiErrorMapping {

  // I N F O R M A T I O N
  private[api] lazy val queriesResource: String = "queries"
  private[api] lazy val simulateResource: String = "simulate"
  private[api] lazy val uuidPath = path[UUID]("uuid")
  private[api] lazy val queriesIdResource: EndpointInput[UUID] = queriesResource / uuidPath
  private[api] lazy val queriesParameter = query[Seq[UUID]]("uuid")
    .example(Seq(
      // see schema.sql
      UUID.fromString("43bbbc0d-fa14-4003-ad15-ef5fdc6c1732"),
      UUID.fromString("ec7381a6-11a1-4261-af95-4b84a1a22bf0"),
      UUID.fromString("dbc20401-2821-44b6-b29d-bbae4313f922"),
      UUID.fromString("b4e5d685-ee44-4f75-aa57-65d84238ee2b"),
      UUID.fromString("7097879d-2138-4d68-9bb5-f576a85f80f2"),
      UUID.fromString("80a309b1-4326-4fac-9e93-6465418d53e5"),
      UUID.fromString("696d07e5-01a2-455b-8aec-e5eaf6d54c6d"),
    ))
    .description("queries to simulate")
  private[api] lazy val queriesSimulateResource: EndpointInput[Seq[UUID]] = queriesResource / simulateResource / queriesParameter
  private[api] lazy val queriesNamePostResource: String = "queries-post-resource"
  private[api] lazy val queriesNameGetResource: String = "queries-get-resource"
  private[api] lazy val queriesNameGetAllResource: String = "queries-get-all-resource"
  private[api] lazy val simulateNameResource: String = "simulate-resource"
  private[api] lazy val queriesDescriptionPostResource: String = "Queries Post Endpoint"
  private[api] lazy val queriesDescriptionGetResource: String = "Queries Get Endpoint"
  private[api] lazy val queriesDescriptionGetAllResource: String = "Queries Get All Endpoint"
  private[api] lazy val simulateDescriptionResource: String = "Simulate Endpoint"

  val queriesExample: Query = Query(UUID.fromString("b7f25fe8-464f-4a91-87d5-830993b2a87d"), "SELECT `personalinfo.address` FROM scalcite")
  val simulateInExample: Json = io.circe.parser.parse(
    """
      |{
      |  "boolean": true,
      |  "string": "true",
      |  "integer": 0
      |}
      |""".stripMargin) match {
    case Left(value) => throw new RuntimeException(value.getMessage())
    case Right(value) => value
  }

  val simulateOutExample: Json = io.circe.parser.parse(
    """
      |[
      |  {
      |    "boolean": true
      |  },
      |  {
      |    "string": "true"
      |  },
      |  {
      |    "integer": 0
      |  }
      |]
      |""".stripMargin) match {
    case Left(value) => throw new RuntimeException(value.getMessage())
    case Right(value) => value
  }

  // E N D P O I N T
  private[api] lazy val queriesPostEndpoint: Endpoint[Query, ScalciteError, Source[ByteString, Any], Source[ByteString, Any]] =
    ApiEndpoint.baseEndpoint.post
      .in(queriesResource)
      .name(queriesNamePostResource)
      .description(queriesDescriptionPostResource)
      .in(jsonBody[Query].example(queriesExample))
      .out(streamBody[Source[ByteString, Any]](schemaFor[Query], CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val queriesGetEndpoint: Endpoint[UUID, ScalciteError, Source[ByteString, Any], Source[ByteString, Any]] =
    ApiEndpoint.baseEndpoint.get
      .in(queriesIdResource)
      .name(queriesNameGetResource)
      .description(queriesDescriptionGetResource)
      .out(streamBody[Source[ByteString, Any]](schemaFor[Query], CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val queriesGetAllEndpoint: Endpoint[Unit, ScalciteError, Source[ByteString, Any], Source[ByteString, Any]] =
    ApiEndpoint.baseEndpoint.get
      .in(queriesResource)
      .name(queriesNameGetAllResource)
      .description(queriesDescriptionGetAllResource)
      .out(streamBody[Source[ByteString, Any]](schemaFor[Query], CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val simulateEndpoint: Endpoint[(Seq[UUID], Json), ScalciteError, Source[ByteString, Any], Source[ByteString, Any]] =
    ApiEndpoint.baseEndpoint.post
      .in(queriesSimulateResource)
      .name(simulateNameResource)
      .description(simulateDescriptionResource)
      .in(jsonBody[Json].example(simulateInExample))
      .out(streamBody[Source[ByteString, Any]](schemaFor[Json], CodecFormat.Json()).example(simulateOutExample.noSpaces))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

}

object ScalciteEndpoint extends ScalciteEndpoint
