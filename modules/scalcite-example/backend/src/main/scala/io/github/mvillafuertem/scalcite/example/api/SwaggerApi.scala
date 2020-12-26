package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.github.mvillafuertem.scalcite.BuildInfoScalcite
import io.github.mvillafuertem.scalcite.example.api.documentation.{ ActuatorEndpoint, ApiEndpoint, ErrorsEndpoint, ScalciteEndpoint }
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._

trait SwaggerApi {

  private lazy val openApi: String = Seq(
    // A C T U A T O R  E N D P O I N T
    ActuatorEndpoint.healthEndpoint,
    // E R R O R S  E N D P O I N T
    ErrorsEndpoint.errorsGetAllEndpoint,
    ErrorsEndpoint.errorsGetEndpoint,
    // S C A L C I T E  E N D P O I N T
    ScalciteEndpoint.queriesPostEndpoint,
    ScalciteEndpoint.queriesGetEndpoint,
    ScalciteEndpoint.queriesGetAllEndpoint,
    // S C A L C I T E  S I M U L A T E  E N D P O I N T
    ScalciteEndpoint.simulateEndpoint
  ).toOpenAPI(BuildInfoScalcite.name, BuildInfoScalcite.version).toYaml

  private lazy val contextPath = "docs"
  private lazy val yamlName    = "docs.yaml"

  val swagger = s"${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/${contextPath}"

  lazy val route: Route = pathPrefix(ApiEndpoint.apiResource / ApiEndpoint.apiVersion) {
    pathPrefix(contextPath) {
      pathEndOrSingleSlash {
        redirect(s"$contextPath/index.html?url=/${ApiEndpoint.apiResource}/${ApiEndpoint.apiVersion}/$contextPath/$yamlName", StatusCodes.PermanentRedirect)
      } ~ path(yamlName) {
        complete(openApi)
      } ~ getFromResourceDirectory("META-INF/resources/webjars/swagger-ui/3.37.2/")
    }
  }

}

object SwaggerApi extends SwaggerApi
