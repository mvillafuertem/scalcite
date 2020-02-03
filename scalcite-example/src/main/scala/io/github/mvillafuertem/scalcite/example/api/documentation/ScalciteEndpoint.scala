package io.github.mvillafuertem.scalcite.example.api.documentation

import akka.stream.scaladsl.Source
import akka.util.ByteString
import io.circe.Json
import io.circe.generic.auto._
import io.github.mvillafuertem.scalcite.example.api.domain.Queries
import sttp.tapir._
import sttp.tapir.json.circe._

trait ScalciteEndpoint {

  // I N F O R M A T I O N
  private[api] lazy val queriesResource: String = "queries"
  private[api] lazy val queriesNameResource: String = "queries-resource"
  private[api] lazy val queriesDescriptionResource: String = "Queries Endpoint"

  private val queriesExample: Queries = Queries(
    Seq(
      "SELECT 'personalinfo.address' FROM PERSON",
      "SELECT 'favoriteFruit' FROM PERSON"
    )
  )
  // E N D P O I N T
  private[api] lazy val queriesEndpoint =
    ApiEndpoint.baseEndpoint.post
      .in(queriesResource)
      .name(queriesNameResource)
      .description(queriesDescriptionResource)
      .in(jsonBody[Queries].example(queriesExample))
      .out(streamBody[Source[ByteString, Any]](schemaFor[Queries], CodecFormat.Json()))
      .errorOut(statusCode)

}

object ScalciteEndpoint extends ScalciteEndpoint
