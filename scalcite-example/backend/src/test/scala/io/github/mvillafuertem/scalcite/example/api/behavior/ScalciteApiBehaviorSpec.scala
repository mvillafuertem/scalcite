package io.github.mvillafuertem.scalcite.example.api.behavior

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import io.github.mvillafuertem.scalcite.example.api.ScalciteApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, Suite}

import scala.concurrent.duration._

trait ScalciteApiBehaviorSpec extends ScalatestRouteTest with Matchers {
  this: Suite =>

  implicit val timeout = RouteTestTimeout(5.seconds.dilated)

  def postQueries(entity: String, expectedStatus: StatusCode, expectedEntity: String, api: => ScalciteApi): Assertion =
    Post(s"/api/v1.0/queries")
      .withEntity(entity) ~>
      addHeader(`Content-Type`(`application/json`)) ~> api.queriesPostRoute ~>
      check {

        // t h e n
        status shouldBe expectedStatus
        responseAs[String] should not be empty
        responseAs[String] shouldBe s"[$expectedEntity\n]"

      }

  def getQueries(uuid: String, expectedStatus: StatusCode, expectedEntity: String, api: => ScalciteApi): Assertion =
    Get(s"/api/v1.0/queries/$uuid") ~>
      addHeader(`Content-Type`(`application/json`)) ~> api.queriesGetRoute ~>
      check {

        // t h e n
        status shouldBe StatusCodes.OK
        responseAs[String] should not be empty
        responseAs[String] shouldBe s"[$expectedEntity\n]"
      }
}
