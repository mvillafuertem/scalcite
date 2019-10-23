package io.github.mvillafuertem.scalcite.example.infrastructure

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import io.github.mvillafuertem.scalcite.flattener.core.JsonFlattener
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.duration._

/**
  * @author Miguel Villafuerte
  */
class RelationalScalciteRepositorySpec extends TestKit(ActorSystem("ScalciteStreamApplication"))
  with FlatSpecLike
  with BeforeAndAfterAll {

  private val scalciteRepository = ScalciteServiceConfiguration.scalciteRepository

  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  behavior of "RelationalScalciteRepositorySpec"

  it should "queryForMap" in {

    // G I V E N
    val person =
      """{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
    val flatten = new JsonFlattener toMap person
    val sql = "SELECT `personalinfo.phone` FROM person"
    val testSink = TestSink.probe[Map[String, Any]]

    // W H E N
    val subscriber: TestSubscriber.Probe[Map[String, Any]] = scalciteRepository.queryForMap(flatten, sql).toMat(testSink)(Keep.right).run()

    // T H E N
    subscriber.ensureSubscription()
      .request(1) // Don't forget this
      .expectNext(5 seconds, Map("personalinfo.phone" -> "+1 (858) 421-2925"))
      .expectComplete()

  }

}

