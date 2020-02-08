package io.github.mvillafuertem.scalcite.example.application

import java.util.UUID

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformerSpec.ScalcitePerformerConfigurationSpec
import io.github.mvillafuertem.scalcite.example.configuration.properties.{CalciteConfigurationProperties, H2ConfigurationProperties}
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import zio.DefaultRuntime

import scala.concurrent.ExecutionContext

final class ScalcitePerformerSpec extends ScalcitePerformerConfigurationSpec {

  behavior of "Scalcite Performer"

  it should "create a query" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(scalcitePerformer.createQuery(query).runHead)

    // t h e n
    actual shouldBe Some(1)

  }

  it should "delete a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Int] = unsafeRun(scalcitePerformer.deleteQueryByUUID(uuid1).runHead)

    // t h e n
    actual shouldBe None

  }

  it should "find a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(scalcitePerformer.findQueryByUUID(uuid1).runHead)

    // t h e n
    actual shouldBe Some(Query(uuid1, ""))

  }

  it should "perform a json" in {

    // g i v e n
    val uuid = UUID.randomUUID()
    val json = Json.obj("boolean" -> Json.fromBoolean(true))


    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(scalcitePerformer.performJson(uuid, json).runHead)

    // t h e n
    actual shouldBe Some(Map(uuid -> ""))

  }

  it should "perform a map" in {

    // g i v e n
    val uuid = UUID.randomUUID()
    val map = Map("boolean" -> Json.fromBoolean(true))


    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(scalcitePerformer.performMap(uuid, map).runHead)

    // t h e n
    actual shouldBe Some(Map(uuid -> ""))

  }

}

object ScalcitePerformerSpec {

  trait ScalcitePerformerConfigurationSpec extends DefaultRuntime
    with AnyFlatSpecLike
    with Matchers with BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()
    private val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()

    private val repository: QueriesRepository = RelationalQueriesRepository(h2ConfigurationProperties.databaseName)
    private val calcite: CalciteRepository = RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

    val scalcitePerformer = new ScalcitePerformer(calcite, repository)

  }

}
