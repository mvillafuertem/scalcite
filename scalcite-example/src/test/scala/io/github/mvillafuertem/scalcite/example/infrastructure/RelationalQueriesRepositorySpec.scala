package io.github.mvillafuertem.scalcite.example.infrastructure

import java.util.UUID

import io.github.mvillafuertem.scalcite.example.configuration.properties.H2ConfigurationProperties
import io.github.mvillafuertem.scalcite.example.infrastructure.RelationalQueriesRepositorySpec.RelationalQueriesRepositoryConfigurationSpec
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import zio.DefaultRuntime

import scala.concurrent.ExecutionContext

final class RelationalQueriesRepositorySpec
  extends RelationalQueriesRepositoryConfigurationSpec {

  behavior of "Relational Queries Repository"

  it should "findById" in {

    // g i v e n
    val id = 0L

    // w h e n
    val actual: Option[QueryDBO] = unsafeRun(repository.findById(id).runHead)

    // t h e n
    actual shouldBe None

  }

  it should "create" in {

    // g i v e n
    val query = QueryDBO(UUID.randomUUID(), "SELECT 'personalinfo.address' FROM PERSON")

    // w h e n
    val actual: Option[Long] = unsafeRun(repository.insert(query).runHead)

    // t h e n
    actual shouldBe Some(1)

  }

  it should "create many queries" in {

    // g i v e n
    val query1 = QueryDBO(UUID.randomUUID(), "SELECT 'personalinfo.address' FROM PERSON")
    val query2 = QueryDBO(UUID.randomUUID(), "SELECT 'favoriteFruit' FROM PERSON")

    // w h e n
    val actual: Seq[Long] = unsafeRun((repository.insert(query1) ++ repository.insert(query2)).runCollect)

    // t h e n
    actual should have size 2

  }


  it should "delete" in {

    // g i v e n
    val uuid1 = UUID.randomUUID()
    val uuid2 = UUID.randomUUID()
    val query1 = QueryDBO(uuid1, "SELECT 'personalinfo.address' FROM PERSON")
    val query2 = QueryDBO(uuid2, "SELECT 'personalinfo.address' FROM PERSON")

    // w h e n
    val actual: Seq[Int] = unsafeRun(
      (repository.insert(query1) *>
        repository.insert(query2) *>
        repository.delete(uuid1)).runCollect)

    // t h e n
    actual should have size 1

  }

}

object RelationalQueriesRepositorySpec {

  trait RelationalQueriesRepositoryConfigurationSpec extends DefaultRuntime
    with AnyFlatSpecLike
    with Matchers {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC
    private val h2ConfigurationProperties: H2ConfigurationProperties = H2ConfigurationProperties()
    val repository = new RelationalQueriesRepository(h2ConfigurationProperties)

  }

}
