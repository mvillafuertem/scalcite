package io.github.mvillafuertem.scalcite.server.application

import io.github.mvillafuertem.scalcite.server.BaseData
import io.github.mvillafuertem.scalcite.server.application.QueriesService.ZQueriesApplication
import io.github.mvillafuertem.scalcite.server.application.QueriesServiceSpec.QueriesServiceConfigurationSpec
import io.github.mvillafuertem.scalcite.server.domain.model.Query
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.RelationalErrorsRepository.ZErrorsRepository
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.RelationalQueriesRepository.ZQueriesRepository
import io.github.mvillafuertem.scalcite.server.infrastructure.repository.{RelationalErrorsRepository, RelationalQueriesRepository}
import zio.{ULayer, ZLayer}

class QueriesServiceSpec extends QueriesServiceConfigurationSpec {

  behavior of "Queries Service"

  it should "create a query" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Query] = unsafeRun(
      QueriesService
        .create(queryString)
        .runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(queryString)

  }

  it should "delete a query by its UUID" in {

    // g i v e n
    // see trait

    // w h e n
    val actual: Option[Int] = unsafeRun(
      (for {
        _      <- QueriesService.create(queryString)
        effect <- QueriesService.deleteByUUID(uuid1)
      } yield effect).runHead
        .provideLayer(env)
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
        _      <- QueriesService.create(queryString)
        effect <- QueriesService.findByUUID(uuid1)
      } yield effect).runHead
        .provideLayer(env)
    )

    // t h e n
    actual shouldBe Some(queryString)

  }

}

object QueriesServiceSpec {

  trait QueriesServiceConfigurationSpec extends BaseData {

    private val queriesRepositoryLayer: ZLayer[Any, Nothing, ZQueriesRepository] =
      ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
        RelationalQueriesRepository.live

    private val errorsRepositoryLayer: ZLayer[Any, Nothing, ZErrorsRepository] =
      ZLayer.succeed(h2ConfigurationProperties.databaseName) >>>
        RelationalErrorsRepository.live

    val env: ULayer[ZQueriesApplication] =
      (queriesRepositoryLayer ++
        errorsRepositoryLayer) >>>
        QueriesService.live
  }

}
