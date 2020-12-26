package io.github.mvillafuertem.scalcite.example.api.documentation

import java.util.UUID
import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.DuplicatedEntity
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import sttp.capabilities.akka.AkkaStreams
import sttp.tapir._

trait ErrorsEndpoint extends ApiErrorMapping {

  // I N F O R M A T I O N
  private[api] lazy val errorsResource: String                = "errors"
  private[api] lazy val uuidPath                              = path[UUID]("uuid")
  private[api] lazy val errorsIdResource: EndpointInput[UUID] = errorsResource / uuidPath
  private[api] lazy val errorsParameter = query[List[UUID]]("uuid")
    .example(
      List(
        UUID.fromString("43bbbc0d-fa14-4003-ad15-ef5fdc6c1732"),
        UUID.fromString("ec7381a6-11a1-4261-af95-4b84a1a22bf0"),
        UUID.fromString("dbc20401-2821-44b6-b29d-bbae4313f922"),
        UUID.fromString("b4e5d685-ee44-4f75-aa57-65d84238ee2b"),
        UUID.fromString("7097879d-2138-4d68-9bb5-f576a85f80f2"),
        UUID.fromString("80a309b1-4326-4fac-9e93-6465418d53e5"),
        UUID.fromString("696d07e5-01a2-455b-8aec-e5eaf6d54c6d")
      )
    )
    .description("errors to simulate")
  private[api] lazy val errorsNameGetResource: String           = "errors-get-resource"
  private[api] lazy val errorsNameGetAllResource: String        = "errors-get-all-resource"
  private[api] lazy val errorsDescriptionGetResource: String    = "Errors Get Endpoint"
  private[api] lazy val errorsDescriptionGetAllResource: String = "Errors Get All Endpoint"

  val errorsExample: ScalciteError = DuplicatedEntity()

  // E N D P O I N T
  private[api] lazy val errorsGetEndpoint: Endpoint[UUID, ScalciteError, Source[ByteString, Any], Any with AkkaStreams] =
    ApiEndpoint.baseEndpoint.get
      .in(errorsIdResource)
      .name(errorsNameGetResource)
      .description(errorsDescriptionGetResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[Query].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

  private[api] lazy val errorsGetAllEndpoint: Endpoint[Unit, ScalciteError, Source[ByteString, Any], Any with AkkaStreams] =
    ApiEndpoint.baseEndpoint.get
      .in(errorsResource)
      .name(errorsNameGetAllResource)
      .description(errorsDescriptionGetAllResource)
      .out(streamBody(AkkaStreams)(Schema(Schema.derived[Query].schemaType), CodecFormat.Json()))
      .errorOut(oneOf(statusInternalServerError, statusDefault))

}

object ErrorsEndpoint extends ErrorsEndpoint
