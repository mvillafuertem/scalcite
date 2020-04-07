package io.github.mvillafuertem.scalcite.example.application

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.application.QueriesService.QueriesApp
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformerSpec.ScalcitePerformerConfigurationSpec
import io.github.mvillafuertem.scalcite.example.domain.repository.{CalciteRepository, QueriesRepository}
import io.github.mvillafuertem.scalcite.example.domain.{QueriesApplication, ScalciteApplication}
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalQueriesRepository}
import zio.{ULayer, ZLayer}

import scala.concurrent.ExecutionContext

final class ScalcitePerformerSpec extends ScalcitePerformerConfigurationSpec {

  behavior of "Scalcite Performer"

  it should "perform a json" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Json] = unsafeRun(
      (for {
        _ <- QueriesService.create(queryBoolean)
        effect <- scalcitePerformer.performJson(json, uuid2)
      } yield effect)
        .runHead
        .provideLayer(env)

    )

    // t h e n
    actual shouldBe Some(json)

  }

  it should "perform a map" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(
      (for {
        _ <- QueriesService.create(queryBoolean)
        effect <- scalcitePerformer.performMap(map, uuid2)
      } yield effect)
        .runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(Map("boolean" -> true))

  }

}

object ScalcitePerformerSpec {

  trait ScalcitePerformerConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

    private val calcite: CalciteRepository = RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

    val scalcitePerformer: ScalciteApplication = ScalcitePerformer(calcite)

    val env: ULayer[QueriesApp] = ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalQueriesRepository.live >>>
      QueriesService.live

  }

}
