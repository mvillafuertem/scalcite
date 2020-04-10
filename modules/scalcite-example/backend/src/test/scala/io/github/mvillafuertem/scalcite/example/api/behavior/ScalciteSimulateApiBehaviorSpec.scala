package io.github.mvillafuertem.scalcite.example.api.behavior

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import io.github.mvillafuertem.scalcite.example.api.ScalciteSimulateApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, Suite}

import scala.concurrent.duration._

trait ScalciteSimulateApiBehaviorSpec extends ScalatestRouteTest with Matchers {
  this: Suite =>

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(5.seconds.dilated)

  def postQueriesSimulate(entity: String, expectedStatus: StatusCode, expectedEntity: String, api: => ScalciteSimulateApi): Assertion =
    Post(s"/api/v1.0/queries/simulate")
      .withEntity(entity) ~>
      addHeader(`Content-Type`(`application/json`)) ~> api.queriesSimulateRoute ~>
      check {

        // t h e n
        status shouldBe expectedStatus
        responseAs[String] should not be empty
        responseAs[String] shouldBe s"$expectedEntity"

      }
}
