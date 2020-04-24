package io.github.mvillafuertem.scalcite.example.infrastructure.repository

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.infrastructure.model.QueryDBO
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository.ZQueriesRepository
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepositorySpec.RelationalQueriesRepositoryConfigurationSpec
import zio.{ ULayer, ZLayer }

final class RelationalQueriesRepositorySpec extends RelationalQueriesRepositoryConfigurationSpec {

  behavior of "Relational Queries Repository"

  it should "find by id" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[QueryDBO] = unsafeRun(
      (for {
        id  <- RelationalQueriesRepository.insert(queryDBO1)
        dbo <- RelationalQueriesRepository.findById(id)
      } yield dbo).runHead
        .provideLayer(env)
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
        _   <- RelationalQueriesRepository.insert(queryDBO1)
        dbo <- RelationalQueriesRepository.findByUUID(uuid1)
      } yield dbo).runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(queryDBO1Expected)

  }

  it should "insert" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Long] = unsafeRun(
      RelationalQueriesRepository
        .insert(queryDBO1)
        .runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(1)

  }

  it should "create many queries" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[Long] = unsafeRun(
      (RelationalQueriesRepository.insert(queryDBO1) ++ RelationalQueriesRepository.insert(queryDBO2)).runCollect
        .provideLayer(env)
    )

    // t h e n
    actual should have size 2

  }

  it should "delete" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[Int] = unsafeRun(
      (RelationalQueriesRepository.insert(queryDBO1) *>
        RelationalQueriesRepository.insert(queryDBO2) *>
        RelationalQueriesRepository.deleteByUUID(uuid1)).runCollect
        .provideLayer(env)
    )

    // t h e n
    actual should have size 1

  }

  it should "find all" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Seq[QueryDBO] = unsafeRun(
      (for {
        _    <- RelationalQueriesRepository.insert(queryDBO1)
        _    <- RelationalQueriesRepository.insert(queryDBO2)
        dbos <- RelationalQueriesRepository.findAll()
      } yield dbos).runCollect
        .provideLayer(env)
    )

    // t h e n
    actual should have size 2

  }

}

object RelationalQueriesRepositorySpec {

  trait RelationalQueriesRepositoryConfigurationSpec extends BaseData {

    val env: ULayer[ZQueriesRepository] = ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
      RelationalQueriesRepository.live

  }

}
