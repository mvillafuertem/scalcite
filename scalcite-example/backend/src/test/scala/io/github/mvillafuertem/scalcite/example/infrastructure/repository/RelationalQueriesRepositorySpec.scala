package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepositorySpec.RelationalQueriesRepositoryConfigurationSpec

import scala.concurrent.ExecutionContext

final class RelationalQueriesRepositorySpec extends RelationalQueriesRepositoryConfigurationSpec {

  behavior of "Relational Queries Repository"

  it should "find by id" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[QueryDBO] = unsafeRun(
      (for {
        id <- repository.insert(queryDBO1)
        dbo <- repository.findById(id)
      } yield dbo).runHead
    )
    // t h e n
    actual shouldBe Some(queryDBO1Expected)

  }

  it should "find by UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[QueryDBO] = unsafeRun(
      (for {
        _ <- repository.insert(queryDBO1)
        dbo <- repository.findByUUID(uuid1)
      } yield dbo).runHead)

    // t h e n
    actual shouldBe Some(queryDBO1Expected)

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

  it should "find all" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[QueryDBO] = unsafeRun(
      (for {
        _ <- repository.insert(queryDBO1)
        _ <- repository.insert(queryDBO2)
        dbos <- repository.findAll()
      } yield dbos).runCollect)

    // t h e n
    actual should have size 2

  }

}

object RelationalQueriesRepositorySpec {

  trait RelationalQueriesRepositoryConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC
    val repository = new RelationalQueriesRepository(h2ConfigurationProperties.databaseName)

  }

}
