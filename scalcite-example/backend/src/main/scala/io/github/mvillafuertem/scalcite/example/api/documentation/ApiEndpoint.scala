package io.github.mvillafuertem.scalcite.example.api.documentation

import sttp.tapir.{Endpoint, EndpointInput, _}

trait ApiEndpoint {

  // I N F O R M A T I O N
  lazy val apiResource: String = "api"
  lazy val apiVersion: String = "v1.0"
  private[api] lazy val apiNameResource: String = "api-resource"
  private[api] lazy val apiDescriptionResource: String = "Api Resources"
  private[api] lazy val baseApiEndpoint: EndpointInput[Unit] = apiResource / apiVersion

  // E N D P O I N T
  private[api] lazy val baseEndpoint: Endpoint[Unit, Unit, Unit, Nothing] =
    endpoint
      .in(baseApiEndpoint)
      .name(apiNameResource)
      .description(apiDescriptionResource)

}

object ApiEndpoint extends ApiEndpoint