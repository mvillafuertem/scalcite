package io.github.mvillafuertem.scalcite.example.api

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.RouteTestTimeout
import akka.stream.Materializer
import akka.testkit.TestDuration
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.api.ScalciteSimulateApiSpec.ScalciteSimulateApiConfigurationSpec
import io.github.mvillafuertem.scalcite.example.api.behavior.{QueriesApiBehaviorSpec, ScalciteSimulateApiBehaviorSpec}
import io.github.mvillafuertem.scalcite.example.api.documentation.ScalciteEndpoint
import io.github.mvillafuertem.scalcite.example.application.{QueriesService, ScalcitePerformer}
import io.github.mvillafuertem.scalcite.example.domain.{QueriesApplication, ScalciteApplication}
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}
import org.scalatest.Succeeded

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


final class ScalciteSimulateApiSpec extends ScalciteSimulateApiConfigurationSpec
  with ScalciteSimulateApiBehaviorSpec
  with QueriesApiBehaviorSpec {

  override implicit val timeout = RouteTestTimeout(10.seconds.dilated)

  val scalciteApi: QueriesApi = QueriesApi()(Materializer(system))
  val scalciteSimulateApi: ScalciteSimulateApi = ScalciteSimulateApi(scalcitePerformer)(Materializer(system))

  behavior of "Scalcite Simulate Api"

  it should "simulate a query" in {

    // g i v e n
    // see trait
    postQueries(
      ScalciteEndpoint.queriesExample.asJson.noSpaces,
      StatusCodes.OK,
      ScalciteEndpoint.queriesExample.asJson.noSpaces,
      scalciteApi
    ) shouldBe Succeeded

    // w h e n
    postQueriesSimulate(
      "{}",
      StatusCodes.OK,
      "[]",
      scalciteSimulateApi
    )

  }

  it should "simulate many queries" in {

    // g i v e n
    // see trait
    Seq(
      postQueries(
        queryString.asJson.noSpaces,
        StatusCodes.OK,
        queryString.asJson.noSpaces,
        scalciteApi
      ),
      postQueries(
        queryBoolean.asJson.noSpaces,
        StatusCodes.OK,
        queryBoolean.asJson.noSpaces,
        scalciteApi
      ),
      postQueries(
        queryInteger.asJson.noSpaces,
        StatusCodes.OK,
        queryInteger.asJson.noSpaces,
        scalciteApi
      )
    ) shouldBe Seq(Succeeded, Succeeded, Succeeded)

    // w h e n
    Post(s"/api/v1.0/queries/simulate?uuid=$uuid1&uuid=$uuid2&uuid=$uuid3")
      .withEntity(ScalciteEndpoint.simulateInExample.noSpaces) ~>
      addHeader(`Content-Type`(`application/json`)) ~> scalciteSimulateApi.queriesSimulateRoute ~>
      check {

        // t h e n
        status shouldBe StatusCodes.OK
        responseAs[String] should not be empty
        responseAs[String] shouldBe
          s"""[{"string":"true"}
             |,{"boolean":true}
             |,{"integer":0}
             |]""".stripMargin

      }

  }

}

object ScalciteSimulateApiSpec {

  trait ScalciteSimulateApiConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val calcite: CalciteRepository = RelationalCalciteRepository(calciteConfigurationProperties.databaseName)
    val scalcitePerformer: ScalciteApplication = ScalcitePerformer(calcite)
  }

}
