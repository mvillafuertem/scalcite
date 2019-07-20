package io.github.mvillafuertem.scalcite

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, Graph}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.TestKit
import io.github.mvillafuertem.mapflablup.JsonFlatten
import org.apache.calcite.jdbc.CalciteConnection
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}
import scalikejdbc.{DB, _}
import scalikejdbc.streams._

import scala.collection.Map
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


final class ScalciteAkkaStreamApplication extends TestKit(ActorSystem("ScalciteStreamApplication"))
  with FlatSpecLike
  with BeforeAndAfterAll {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  behavior of "ScalciteStreamApplication"


  it should "perform with akka stream" in {


    // G I V E N
    val testSink = TestSink.probe[Map[String, Any]]

    // W H E N
    val subscriber: TestSubscriber.Probe[Map[String, Any]] = Source.fromPublisher(databasePublisher).toMat(testSink)(Keep.right).run()

    // T H E N
    subscriber.ensureSubscription()
      .request(1) // Don't forget this
      .expectNext(5 seconds, Map("personalinfo.phone" -> "+1 (858) 421-2925"))
      .expectComplete()

  }


  private def databasePublisher: DatabasePublisher[Predef.Map[String, Any]] = DB readOnlyStream {



//    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
//      enabled = true,
//      singleLineMode = true,
//    )

    val connectionPoolSettings: ConnectionPoolSettings = ConnectionPoolSettings(
      initialSize = 1,
      maxSize = 5,
      connectionTimeoutMillis = 3000L,
      validationQuery = "select 1 from dual",
      driverName = "org.apache.calcite.jdbc.Driver")

    ConnectionPool.singleton(
      s"jdbc:calcite:?caseSensitive=false;defaultSchema=json;lex=MYSQL", "", "", connectionPoolSettings)


    val person = """{"_id":"5c5f1f313fcc6e3084fbe65e","index":0,"guid":"f3b5960b-f3e1-4556-9a5d-f552afe204e7","isActive":true,"balance":"$2,809.92","picture":"http://placehold.it/32x32","age":28,"eyeColor":"blue","personalinfo":{"name":"Elliott Kaufman","gender":"male","phone":"+1 (858) 421-2925","email":"elliottkaufman@spacewax.com","address":"952 Cropsey Avenue, Tyro, Guam, 1787","company":{"name":"SPACEWAX"}},"about":"Labore tempor cupidatat nulla veniam ea veniam aliqua ea. Ad id id dolor enim quis amet irure ad occaecat. Quis enim enim esse mollit. Et officia officia ea consectetur deserunt eiusmod nisi ex culpa consectetur.\r\n","registered":"2015-03-28T06:35:33 -01:00","location":{"latitude":78.370719,"longitude":-137.117139},"greeting":"Hello, Elliott Kaufman! You have 5 unread messages.","favoriteFruit":"strawberry"}"""
    val flatten = new JsonFlatten toMap person

    SQL("SELECT `personalinfo.phone` FROM person")
      .map(_.toMap()).iterator()
      .withDBSessionForceAdjuster(session => {

        println(session)

        session.connection.unwrap(classOf[CalciteConnection])
          .getRootSchema
          .add("person", JsonTable(flatten))

      })
  }

}
