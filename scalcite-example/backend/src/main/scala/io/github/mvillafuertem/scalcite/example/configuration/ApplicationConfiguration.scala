package io.github.mvillafuertem.scalcite.example.configuration

import io.github.mvillafuertem.scalcite.example.application.{ErrorsService, QueriesService, ScalcitePerformer}
import io.github.mvillafuertem.scalcite.example.configuration.InfrastructureConfiguration.ZInfrastructureConfiguration
import io.github.mvillafuertem.scalcite.example.domain.{ErrorsApplication, QueriesApplication, ScalciteApplication}
import io.github.mvillafuertem.scalcite.example.infrastructure.repository.{RelationalCalciteRepository, RelationalErrorsRepository, RelationalQueriesRepository}
import zio.{Has, URIO, ZIO, ZLayer}


final class ApplicationConfiguration(infrastructureConfiguration: InfrastructureConfiguration) {

  val queriesApplication: QueriesApplication =
    QueriesService(RelationalQueriesRepository(""))

  val errorsApplication: ErrorsApplication =
    ErrorsService(RelationalErrorsRepository(""))


  val scalciteApplication: ScalciteApplication =
    ScalcitePerformer(queriesApplication, RelationalCalciteRepository(""))

}

object ApplicationConfiguration {

  def apply(infrastructureConfiguration: InfrastructureConfiguration): ApplicationConfiguration =
    new ApplicationConfiguration(infrastructureConfiguration)

  type ZApplicationConfiguration = Has[ApplicationConfiguration]

  val queriesApplication: URIO[ZApplicationConfiguration, QueriesApplication] =
    ZIO.access(_.get.queriesApplication)

  val errorsApplication: URIO[ZApplicationConfiguration, ErrorsApplication] =
    ZIO.access(_.get.errorsApplication)

  val scalciteApplication: URIO[ZApplicationConfiguration, ScalciteApplication] =
    ZIO.access(_.get.scalciteApplication)

  val live: ZLayer[ZInfrastructureConfiguration, Nothing, ZApplicationConfiguration] =
    ZLayer.fromService[InfrastructureConfiguration, ApplicationConfiguration](
      infrastructureConfiguration => ApplicationConfiguration(infrastructureConfiguration))

}


