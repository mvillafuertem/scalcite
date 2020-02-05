//package io.github.mvillafuertem.scalcite.example.application
//
//import akka.actor.ActorSystem
//import akka.stream.scaladsl.{Keep, Source}
//import akka.stream.testkit.TestSubscriber
//import akka.stream.testkit.scaladsl.TestSink
//import akka.testkit.TestKit
//import io.github.mvillafuertem.scalcite.example.configuration.properties.CalciteConfigurationProperties
//import io.github.mvillafuertem.scalcite.example.domain.repository.QueriesRepository
//import io.github.mvillafuertem.scalcite.example.infrastructure.RelationalScalciteRepository
//import org.scalamock.scalatest.MockFactory
//import org.scalatest.BeforeAndAfterAll
//import org.scalatest.flatspec.AnyFlatSpecLike
//import org.scalatest.matchers.should.Matchers
//
//import scala.concurrent.duration._
//
///**
// * @author Miguel Villafuerte
// */
//class ScalciteApplicationImplSpec extends TestKit(ActorSystem("ScalciteStreamApplication"))
//  with AnyFlatSpecLike
//  with BeforeAndAfterAll
//  with Matchers
//  with MockFactory{
//
//  private lazy val sqlRepository = mock[QueriesRepository[Source]]
//  private lazy val scalciteApplication = ScalciteApplicationImpl(sqlRepository, new RelationalScalciteRepository(CalciteConfigurationProperties()))
//
//  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)
//
//  behavior of "ScalciteApplicationImplSpec"
//
//  it should "performMap" in {
//
//    // G I V E N
//    val id = 1234567890L
//    val json ="""{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.","registered":"2015-03-28T06:35:33 -01:00","greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
//    val testSink = TestSink.probe[String]
//
//    (sqlRepository.findById _)
//      .expects(id)
//      .returns(Source(List(Map("SQL" -> "SELECT `personalinfo.phone` FROM person"))))
//      .once()
//
//    // W H E N
//    val subscriber: TestSubscriber.Probe[String] = scalciteApplication.performJson(id, json).toMat(testSink)(Keep.right).run()
//
//    // T H E N
//    subscriber.ensureSubscription()
//      .request(1) // Don't forget this
//      .expectNext(5 seconds, """{"personalinfo":{"phone":"+1 (858) 421-2925"}}""")
//      .expectComplete()
//
//  }
//
//  it should "performJson" in {
//
//    // G I V E N
//    val id = 1234567890L
//    val json ="""{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.","registered":"2015-03-28T06:35:33 -01:00","greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
//    val testSink = TestSink.probe[String]
//
//    (sqlRepository.findById _)
//      .expects(id)
//      .returns(Source(List(Map("SQL" -> "SELECT `personalinfo.phone` FROM person"))))
//      .once()
//
//    // W H E N
//    val subscriber: TestSubscriber.Probe[String] = scalciteApplication.performJson(id, json).toMat(testSink)(Keep.right).run()
//
//    // T H E N
//    subscriber.ensureSubscription()
//      .request(1) // Don't forget this
//      .expectNext(5 seconds, """{"personalinfo":{"phone":"+1 (858) 421-2925"}}""")
//      .expectComplete()
//
//  }
//
//}
