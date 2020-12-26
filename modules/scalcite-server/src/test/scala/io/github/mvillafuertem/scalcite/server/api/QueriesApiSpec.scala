package io.github.mvillafuertem.scalcite.server.api

import akka.http.scaladsl.model.StatusCodes
import akka.stream.Materializer
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.server.BaseData
import io.github.mvillafuertem.scalcite.server.api.QueriesApiSpec.QueriesApiConfigurationSpec
import io.github.mvillafuertem.scalcite.server.api.behavior.QueriesApiBehaviorSpec
import io.github.mvillafuertem.scalcite.server.api.documentation.ScalciteEndpoint
import io.github.mvillafuertem.scalcite.server.application.QueriesService
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.{ RelationalErrorsRepository, RelationalQueriesRepository }
import org.scalatest.Succeeded

final class QueriesApiSpec extends QueriesApiConfigurationSpec with QueriesApiBehaviorSpec {

  val scalciteApi: QueriesApi =
    QueriesApi(
      QueriesService(RelationalQueriesRepository(h2ConfigurationProperties.databaseName), RelationalErrorsRepository(h2ConfigurationProperties.databaseName))
    )(Materializer(system))

  behavior of "Scalcite Api"

  it should "create a query" in postQueries(
    ScalciteEndpoint.queriesExample.asJson.noSpaces,
    StatusCodes.OK,
    ScalciteEndpoint.queriesExample.asJson.noSpaces,
    scalciteApi
  )

  it should "get a query" in {

    // g i v e n
    // see trait
    postQueries(
      ScalciteEndpoint.queriesExample.asJson.noSpaces,
      StatusCodes.OK,
      ScalciteEndpoint.queriesExample.asJson.noSpaces,
      scalciteApi
    ) shouldBe Succeeded

    // w h e n
    getQueries(
      ScalciteEndpoint.queriesExample.uuid.toString,
      StatusCodes.OK,
      ScalciteEndpoint.queriesExample.asJson.noSpaces,
      scalciteApi
    )
  }

}

object QueriesApiSpec {

  trait QueriesApiConfigurationSpec extends BaseData {}
}
