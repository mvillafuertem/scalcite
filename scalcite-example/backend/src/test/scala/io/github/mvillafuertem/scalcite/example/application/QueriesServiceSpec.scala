package io.github.mvillafuertem.scalcite.example.application

import io.github.mvillafuertem.scalcite.example.BaseData
import io.github.mvillafuertem.scalcite.example.application.QueriesServiceSpec.QueriesServiceConfigurationSpec
import io.github.mvillafuertem.scalcite.example.domain.model.Query
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.RelationalQueriesRepository

import scala.concurrent.ExecutionContext

class QueriesServiceSpec extends QueriesServiceConfigurationSpec {

  behavior of "Queries Service"

  it should "create a query" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(QueriesService.create(queryString)
      .runHead
      .provideLayer(RelationalQueriesRepository.live >>> QueriesService.live)
      .provide(h2ConfigurationProperties.databaseName))

    // t h e n
    actual shouldBe Some(queryString)

  }

  it should "delete a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Int] = unsafeRun(
      (for {
        _ <- QueriesService.create(queryString)
        effect <- QueriesService.deleteByUUID(uuid1)
      } yield effect)
        .runHead
        .provideLayer(RelationalQueriesRepository.live >>> QueriesService.live)
        .provide(h2ConfigurationProperties.databaseName)
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
        _ <- QueriesService.create(queryString)
        effect <- QueriesService.findByUUID(uuid1)
      } yield effect)
        .runHead
        .provideLayer(RelationalQueriesRepository.live >>> QueriesService.live)
        .provide(h2ConfigurationProperties.databaseName)
    )

    // t h e n
    actual shouldBe Some(queryString)

  }

}

object QueriesServiceSpec {

  trait QueriesServiceConfigurationSpec extends BaseData {

    private implicit val executionContext: ExecutionContext = platform.executor.asEC

  }

}
