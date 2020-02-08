package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.circe.Json
import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.configuration.properties.CalciteConfigurationProperties
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalCalciteRepositorySpec.RelationalCalciteRepositoryConfigurationSpec
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import zio.DefaultRuntime

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
    actual shouldBe Some(Map("boolean" -> true))

  }
}

object RelationalCalciteRepositorySpec {

  trait RelationalCalciteRepositoryConfigurationSpec extends DefaultRuntime
    with AnyFlatSpecLike
    with Matchers
    with BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC
    private val calciteConfigurationProperties: CalciteConfigurationProperties = CalciteConfigurationProperties()
    val calcite = new RelationalCalciteRepository(calciteConfigurationProperties.databaseName)

    ConnectionPool.add(Symbol(calciteConfigurationProperties.databaseName),
      calciteConfigurationProperties.url,
      calciteConfigurationProperties.user,
      calciteConfigurationProperties.password,
      ConnectionPoolSettings(
        initialSize = calciteConfigurationProperties.initialSize,
        maxSize = calciteConfigurationProperties.maxSize,
        connectionTimeoutMillis = calciteConfigurationProperties.connectionTimeoutMillis,
        validationQuery = calciteConfigurationProperties.validationQuery,
        driverName = calciteConfigurationProperties.driverName)
    )

  }
}
