package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.configuration.properties.H2ConfigurationProperties
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepositorySpec.RelationalQueriesRepositoryConfigurationSpec
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import scalikejdbc.{ConnectionPool, ConnectionPoolSettings}
import zio.DefaultRuntime

import scala.concurrent.ExecutionContext

final class RelationalQueriesRepositorySpec
  extends RelationalQueriesRepositoryConfigurationSpec {

  behavior of "Relational Queries Repository"

  it should "findById" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[QueryDBO] = unsafeRun(repository.findById(id).runHead)

    // t h e n
    actual shouldBe None

  }

  it should "insert" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Long] = unsafeRun(repository.insert(queryDBO1).runHead)

    // t h e n
    actual shouldBe Some(1)

  }

  it should "create many queries" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[Long] = unsafeRun((repository.insert(queryDBO1) ++ repository.insert(queryDBO2)).runCollect)

    // t h e n
    actual should have size 2

  }


  it should "delete" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[Int] = unsafeRun(
      (repository.insert(queryDBO1) *>
        repository.insert(queryDBO2) *>
        repository.deleteByUUID(uuid1)).runCollect)

    // t h e n
    actual should have size 1

  }

}

object RelationalQueriesRepositorySpec {

  trait RelationalQueriesRepositoryConfigurationSpec extends DefaultRuntime
    with AnyFlatSpecLike
    with Matchers with BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC
    private val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()
    val repository = new RelationalQueriesRepository(h2ConfigurationProperties.databaseName)

    ConnectionPool.add(Symbol(h2ConfigurationProperties.databaseName),
      h2ConfigurationProperties.url,
      h2ConfigurationProperties.user,
      h2ConfigurationProperties.password,
      ConnectionPoolSettings(
        initialSize = h2ConfigurationProperties.initialSize,
        maxSize = h2ConfigurationProperties.maxSize,
        connectionTimeoutMillis = h2ConfigurationProperties.connectionTimeoutMillis,
        validationQuery = h2ConfigurationProperties.validationQuery,
        driverName = h2ConfigurationProperties.driverName
      )
    )

  }

}
