package io.github.mvillafuertem.scalcite.example.application

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import io.github.mvillafuertem.scalcite.example.domain.repository.{ScalciteRepository, SqlRepository}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

/**
 * @author Miguel Villafuerte
 */
class ScalciteApplicationImplSpec extends TestKit(ActorSystem("ScalciteStreamApplication"))
  with AnyFlatSpecLike
  with BeforeAndAfterAll
  with Matchers
  with MockFactory{

  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private lazy val scalciteRepository = mock[ScalciteRepository[Source]]
  private lazy val sqlRepository = mock[SqlRepository[Source]]
  private lazy val scalciteApplication = ScalciteApplicationImpl(sqlRepository, ScalciteServiceConfiguration.scalciteRepository)

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  behavior of "ScalciteApplicationImplSpec"

  it should "perform" in {

    // G I V E N
    val id = 1234567890L
    val json ="""{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
    val testSink = TestSink.probe[String]

    (sqlRepository.findById _)
      .expects(id)
      .returns(Source(List(Map("sql" -> "SELECT `personalinfo.phone` FROM person"))))
      .once()

//    (scalciteRepository.queryForMap _)
//      .expects(Map("personalinfo.company.name" -> "SPACEWAX",
//        "registered" -> "2015-03-28T06:35:33 -01:00",
//        "guid" -> "f3b5960b-f3e1-4556-9a5d-f552afe204e7",
//        "location.latitude" -> 78.370719,
//        "_id" -> "5c5f1f313fcc6e3084fbe65e",
//        "balance" -> "$2,809.92",
//        "personalinfo.phone" -> "+1 (858) 421-2925",
//        "age" -> 28,
//        "personalinfo.name" -> "Elliott Kaufman",
//        "location.longitude" -> -137.117139,
//        "favoriteFruit" -> "strawberry",
//        "personalinfo.gender" -> "male",
//        "isActive" -> true,
//        "greeting" -> "Hello, Elliott Kaufman! You have 5 unread messages.",
//        "picture" -> "http://placehold.it/32x32",
//        "personalinfo.address" -> "952 Cropsey Avenue, Tyro, Guam, 1787",
//        "about" -> "Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.",
//        "eyeColor" -> "blue",
//        "index" -> 0,
//        "personalinfo.email" ->
//          "elliottkaufman@spacewax.com"), "SELECT `personalinfo.phone` FROM person")
//      .returns(Source(List(Map("personalinfo.phone" -> ""))))
//      .once()


    // W H E N
    val subscriber: TestSubscriber.Probe[String] = scalciteApplication.perform(id, json).toMat(testSink)(Keep.right).run()

    // T H E N
    subscriber.ensureSubscription()
      .request(1) // Don't forget this
      .expectNext(5 seconds, """{"personalinfo":{"phone":"+1 (858) 421-2925"}}""")
      .expectComplete()

  }

}
