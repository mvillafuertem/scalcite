package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepository.ZCalciteRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepositorySpec.RelationalCalciteRepositoryConfigurationSpec
import zio.{ FiberFailure, ULayer, ZLayer }

final class RelationalCalciteRepositorySpec extends RelationalCalciteRepositoryConfigurationSpec {

  behavior of "Relational Calcite Repository"

  it should "query for map" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[collection.Map[String, Any]] = unsafeRun(
      RelationalCalciteRepository
        .queryForMap(map, queryBooleanValue)
        .runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(Map("boolean" -> true))

  }

  it should "query for json" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = unsafeRun(
      RelationalCalciteRepository
        .queryForJson(json, queryBooleanValue)
        .runHead
        .provideLayer(env)
    )
    // t h e n
    actual shouldBe Some(json)

  }

  it should "query for json tree" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = unsafeRun(
      RelationalCalciteRepository
        .queryForJson(jsonTree, queryBooleanTreeValue)
        .runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(jsonTree)

  }

  it should "query for json empty" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = intercept[FiberFailure](
      unsafeRun(
        RelationalCalciteRepository
          .queryForJson(jsonEmpty, queryBooleanValue)
          .runHead
          .provideLayer(env)
      )
    )

    // t h e n
    actual.cause.map(a => a shouldBe an[Unknown])

  }

}

object RelationalCalciteRepositorySpec {

  trait RelationalCalciteRepositoryConfigurationSpec extends BaseData {

    val env: ULayer[ZCalciteRepository] = ZLayer.succeed(calciteConfigurationProperties.databaseName) >>> RelationalCalciteRepository.live

  }
}
