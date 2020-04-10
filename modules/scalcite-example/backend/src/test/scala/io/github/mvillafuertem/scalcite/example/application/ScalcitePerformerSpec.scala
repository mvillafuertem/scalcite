package io.github.mvillafuertem.scalcite.example.application

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformer.ZScalciteApplication
import io.github.mvillafuertem.scalcite.example.application.ScalcitePerformerSpec.ScalcitePerformerConfigurationSpec
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepository.ZCalciteRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.ZQueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalErrorsRepository, RelationalQueriesRepository}
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
        effect <- ScalcitePerformer.performJson(json, uuid2)
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
        effect <- ScalcitePerformer.performMap(map, uuid2)
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

    private val queriesRepositoryLayer: ZLayer[Any, Nothing, ZQueriesRepository] =
      ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
        RelationalQueriesRepository.live

    private val errorsRepositoryLayer: ZLayer[Any, Nothing, ZErrorsRepository] =
      ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
        RelationalErrorsRepository.live

    private val queriesApplicationLayer: ULayer[ZQueriesApplication] =
      (queriesRepositoryLayer ++ errorsRepositoryLayer) >>>
        QueriesService.live

    private val calciteRepositoryLayer: ULayer[ZCalciteRepository] =
      ZLayer.succeed(calciteConfigurationProperties.databaseName) >>>
        RelationalCalciteRepository.live

    val env: ULayer[ZScalciteApplication with ZQueriesApplication] =
      queriesApplicationLayer ++
        errorsRepositoryLayer ++
        calciteRepositoryLayer >>>
      ScalcitePerformer.live ++ queriesApplicationLayer

  }

}
