package io.github.mvillafuertem.scalcite.example.application

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformerSpec.ScalcitePerformerConfigurationSpec
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}

import scala.concurrent.ExecutionContext

final class ScalcitePerformerSpec extends ScalcitePerformerConfigurationSpec {

  behavior of "Scalcite Performer"

  it should "create a query" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(scalcitePerformer.createQuery(queryString).runHead)

    // t h e n
    actual shouldBe Some(queryString)

  }

  it should "delete a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Int] = unsafeRun(
      (for {
        _ <- scalcitePerformer.createQuery(queryString)
        effect <- scalcitePerformer.deleteQueryByUUID(uuid1)
      } yield effect).runHead
    )

    // t h e n
    actual shouldBe Some(1)

  }

  it should "find a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(
      (for {
        _ <- scalcitePerformer.createQuery(queryString)
        effect <- scalcitePerformer.findQueryByUUID(uuid1)
      } yield effect).runHead
    )

    // t h e n
    actual shouldBe Some(queryString)

  }

  it should "perform a json" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(
      (for {
        _ <- scalcitePerformer.createQuery(queryBoolean)
        effect <- scalcitePerformer.performJson(uuid1, json)
      } yield effect).runHead
    )

    // t h e n
    actual shouldBe Some(Map("boolean" -> true))

  }

  it should "perform a map" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(
      (for {
        _ <- scalcitePerformer.createQuery(queryBoolean)
        effect <- scalcitePerformer.performMap(uuid1, map)
      } yield effect).runHead
    )

    // t h e n
    actual shouldBe Some(Map("boolean" -> true))

  }

}

object ScalcitePerformerSpec {

  trait ScalcitePerformerConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val repository: QueriesRepository = RelationalQueriesRepository(h2ConfigurationProperties.databaseName)
    private val calcite: CalciteRepository = RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

    val scalcitePerformer = new ScalcitePerformer(calcite, repository)

  }

}
