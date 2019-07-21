package io.github.mvillafuertem.scalcite.example.infrastructure

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Keep
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import io.github.mvillafuertem.scalcite.example.configuration.ScalciteServiceConfiguration
import io.github.mvillafuertem.scalcite.example.domain.model.ScalciteSql
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike}

import scala.concurrent.duration._

/**
  * @author Miguel Villafuerte
  */
class RelationalSqlRepositorySpec extends TestKit(ActorSystem("ScalciteStreamApplication"))
  with FlatSpecLike
  with BeforeAndAfterAll {

  private val sqlRepository = ScalciteServiceConfiguration.sqlRepository

  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  behavior of "RelationalSqlRepositorySpec"


  it should "insert" in {

    // G I V E N
    val scalciteSql: ScalciteSql = ScalciteSql(2, "SELECT * FROM person")
    val testSink = TestSink.probe[Map[String, Any]]

    // W H E N
    sqlRepository.insert2(scalciteSql)
//    val actual: TestSubscriber.Probe[Map[String, Any]] = sqlRepository.insert(scalciteSql).toMat(testSink)(Keep.right).run()
//
//    // T H E N
//    actual.ensureSubscription()
//      .request(1) // Don't forget this
//      .expectNext(5 seconds, Map("personalinfo.phone" -> "+1 (858) 421-2925"))
//      .expectComplete()

  }

  it should "delete" in {

  }

  it should "findById" in {

  }

}
