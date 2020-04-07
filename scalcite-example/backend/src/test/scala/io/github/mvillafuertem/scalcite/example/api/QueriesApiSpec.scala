package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.model.StatusCodes
import akka.stream.Materializer
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.api.QueriesApiSpec.QueriesApiConfigurationSpec
import io.github.mvillafuertem.scalcite.example.api.behavior.QueriesApiBehaviorSpec
import io.github.mvillafuertem.scalcite.example.api.documentation.ScalciteEndpoint
import io.github.mvillafuertem.scalcite.example.application.QueriesService
import io.github.mvillafuertem.scalcite.example.domain.QueriesApplication
import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository
import org.scalatest.Succeeded

import scala.concurrent.ExecutionContext

final class QueriesApiSpec extends QueriesApiConfigurationSpec with QueriesApiBehaviorSpec {

  val scalciteApi: QueriesApi = QueriesApi()(Materializer(system))

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

  trait QueriesApiConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val repository: QueriesRepository[QueryDBO] = RelationalQueriesRepository(h2ConfigurationProperties.databaseName)

  }
}
