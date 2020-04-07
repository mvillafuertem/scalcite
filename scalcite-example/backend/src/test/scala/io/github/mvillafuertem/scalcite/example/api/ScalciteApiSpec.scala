package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.model.StatusCodes
import akka.stream.Materializer
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.api.ScalciteApiSpec.ScalciteApiConfigurationSpec
import io.github.mvillafuertem.scalcite.example.api.behavior.ScalciteApiBehaviorSpec
import io.github.mvillafuertem.scalcite.example.api.documentation.ScalciteEndpoint
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}
import org.scalatest.Succeeded

import scala.concurrent.ExecutionContext

final class ScalciteApiSpec extends ScalciteApiConfigurationSpec with ScalciteApiBehaviorSpec {

  val scalciteApi: ScalciteApi = ScalciteApi(scalcitePerformer)(Materializer(system))

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

object ScalciteApiSpec {

  trait ScalciteApiConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val repository: QueriesRepository = RelationalQueriesRepository(h2ConfigurationProperties.databaseName)
    private val calcite: CalciteRepository = RelationalCalciteRepository(calciteConfigurationProperties.databaseName)
    val scalcitePerformer = new ScalcitePerformer(calcite, repository)

  }
}
