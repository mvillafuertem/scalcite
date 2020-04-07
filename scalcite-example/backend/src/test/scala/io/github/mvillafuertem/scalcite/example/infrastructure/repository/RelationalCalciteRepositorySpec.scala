package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.domain.error.ScalciteError.Unknown
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepositorySpec.RelationalCalciteRepositoryConfigurationSpec
import zio.FiberFailure

import scala.concurrent.ExecutionContext

final class RelationalCalciteRepositorySpec extends RelationalCalciteRepositoryConfigurationSpec {

  behavior of "Relational Calcite Repository"

  it should "query for map" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = unsafeRun(calcite.queryForMap(map, queryBooleanValue).runHead)

    // t h e n
    actual shouldBe Some(Map("boolean" -> true))

  }

  it should "query for json" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = unsafeRun(calcite.queryForJson(json, queryBooleanValue).runHead)

    // t h e n
    actual shouldBe Some(json)

  }

  it should "query for json tree" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = unsafeRun(calcite.queryForJson(jsonTree, queryBooleanTreeValue).runHead)

    // t h e n
    actual shouldBe Some(jsonTree)

  }

  it should "query for json empty" in {

    // g i v e n
    // see trait

    // w h e n
    val actual = intercept[FiberFailure](unsafeRun(calcite.queryForJson(jsonEmpty, queryBooleanValue).runHead))

    // t h e n
    actual.cause.map { a =>
      a shouldBe an[Unknown]
    }

  }

}

object RelationalCalciteRepositorySpec {

  trait RelationalCalciteRepositoryConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC
    val calcite = new RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

  }
}
